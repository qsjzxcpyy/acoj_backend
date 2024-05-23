package com.qsj.acoj.judge;

import cn.hutool.json.JSONUtil;
import com.qsj.acoj.common.ErrorCode;
import com.qsj.acoj.judge.codesandbox.CodeSandbox;
import com.qsj.acoj.judge.codesandbox.CodeSandboxFactory;
import com.qsj.acoj.judge.codesandbox.CodeSandboxProxy;
import com.qsj.acoj.exception.BusinessException;
import com.qsj.acoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.qsj.acoj.judge.codesandbox.model.ExecuteCodeResponse;
import com.qsj.acoj.judge.strategy.JudgeContext;
import com.qsj.acoj.model.dto.question.JudgeCase;
import com.qsj.acoj.judge.codesandbox.model.JudgeInfo;
import com.qsj.acoj.model.entity.Question;
import com.qsj.acoj.model.entity.QuestionSubmit;
import com.qsj.acoj.model.enums.QuestionSubmitStatusEnum;
import com.qsj.acoj.service.QuestionService;
import com.qsj.acoj.service.QuestionSubmitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JudgeSeviceImpl implements JudgeService {
    @Value("${codeSandbox.type:example}")
    private String type;
    @Resource
    private QuestionService questionService;
    @Resource
    private QuestionSubmitService questionSubmitService;
    @Resource
    private JudgeManage judgeManage;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        /*
        1. 进行检验，检验题目提交是否存在， 检验题目是否存在
        3，题目的标准输出数据，题目的限制要求
        4. 获得用户提交的代码和语言
        5. 通过判断当前题目提交的状态来确定是否放行，放行后立即修改状态
        5. 调用代码沙箱进行判题，通过配置文件指定使用哪个代码沙箱。
        6. 根据沙箱返回的结果和状态进行判断用户是否回答是否正确，填充返回结果。
           6.0 沙箱的运行状态是否成功
           6.1 先判断用例条数是否与预期的相同
           6.2 依次判断输出用例
           6.3 根据题目限制进行判断


         */
        // 进行检验，检验题目提交是否存在， 检验题目是否存在
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }

        Question question = questionService.getById(questionSubmit.getQuestionId());
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目信息不存在");
        }
        //判断该判题任务是否已经在评测
        if (!QuestionSubmitStatusEnum.WAITTING.getValue().equals(questionSubmit.getStatus())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在评测中~");
        }

        // 更改评测状态
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmit.getId());
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目提交状态更新失败");
        }

        //获得question: JudgeConfig, 评测用例,输出用例
        List<JudgeCase> listJudgeCase = JSONUtil.toList(question.getJudgeCase(), JudgeCase.class);
        List<String> listInputCase = listJudgeCase.stream().map(JudgeCase::getInput).collect(Collectors.toList());

        //获得用户提交的代码和语言
        String code = questionSubmit.getCode();
        String language = questionSubmit.getLanguage();

        //调用代码沙箱评测题目
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        CodeSandboxProxy codeSandboxProxy = new CodeSandboxProxy(codeSandbox);
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder().code(code).intputList(listInputCase).language(language).build();
        ExecuteCodeResponse executeCodeResponse = codeSandboxProxy.executeCode(executeCodeRequest);

        //依据代码沙箱返回的信息进行判题，并更新题目提交信息。
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setInputList(listInputCase);
        judgeContext.setOutputList(executeCodeResponse.getOutputList());
        judgeContext.setQuestion(question);
        judgeContext.setStatus(executeCodeResponse.getStatue());
        judgeContext.setQuestionSubmit(questionSubmit);
        judgeContext.setJudgeCase(listJudgeCase);

        JudgeInfo finalJudgeInfo = judgeManage.doJudge(judgeContext);

        //设置题目判题情况，设置判题状态
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        questionSubmitUpdate.setId(questionSubmitId);
        String finalJudgeInfoStr = JSONUtil.toJsonStr(finalJudgeInfo);
        questionSubmitUpdate.setJudgeInfo(finalJudgeInfoStr);
        boolean isUpdate = questionSubmitService.updateById(questionSubmitUpdate);
        if(!isUpdate){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"题目状态更新失败");
        }

        QuestionSubmit questionSubmitResult = questionSubmitService.getById(questionSubmitId);
        return questionSubmitResult;


    }
}
