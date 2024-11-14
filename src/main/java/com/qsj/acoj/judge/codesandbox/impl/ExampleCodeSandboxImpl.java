package com.qsj.acoj.judge.codesandbox.impl;

import com.qsj.acoj.judge.codesandbox.CodeSandbox;
import com.qsj.acoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.qsj.acoj.judge.codesandbox.model.ExecuteCodeResponse;
import com.qsj.acoj.judge.codesandbox.model.JudgeInfo;
import com.qsj.acoj.model.enums.JudgeInfoMessageEnum;
import com.qsj.acoj.model.enums.QuestionSubmitStatusEnum;

import java.util.List;

/**
 * 跑通代码逻辑的代码沙箱
 */
public class ExampleCodeSandboxImpl implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> intputList = executeCodeRequest.getIntputList();
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();

        executeCodeResponse.setOutputList(intputList);
        executeCodeResponse.setMessage("测试沙箱代码运行完成");
//        executeCodeResponse.setStatue(QuestionSubmitStatusEnum.SUCCEED.getValue());

        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getText());
        judgeInfo.setMemory(100l);
        judgeInfo.setTime(100l);

        executeCodeResponse.setJudgeInfo(judgeInfo);

        return executeCodeResponse;

    }
}
