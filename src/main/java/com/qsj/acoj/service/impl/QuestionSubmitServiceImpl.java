package com.qsj.acoj.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qsj.acoj.common.ErrorCode;
import com.qsj.acoj.constant.CommonConstant;
import com.qsj.acoj.constant.QuestionConstant;
import com.qsj.acoj.exception.BusinessException;
import com.qsj.acoj.judge.JudgeService;
import com.qsj.acoj.model.dto.question.QuestionSubmitResp;
import com.qsj.acoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.qsj.acoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.qsj.acoj.model.entity.Question;
import com.qsj.acoj.model.entity.QuestionSubmit;
import com.qsj.acoj.model.entity.User;
import com.qsj.acoj.model.enums.QuestionSubmitLanguageEnum;
import com.qsj.acoj.model.enums.QuestionSubmitStatusEnum;
import com.qsj.acoj.model.vo.QuestionSubmitVO;
import com.qsj.acoj.model.vo.QuestionVO;
import com.qsj.acoj.model.vo.UserVO;
import com.qsj.acoj.service.QuestionService;
import com.qsj.acoj.service.QuestionSubmitService;
import com.qsj.acoj.mapper.QuestionSubmitMapper;
import com.qsj.acoj.service.UserService;
import com.qsj.acoj.utils.RequestTokenUtils;
import com.qsj.acoj.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author qsj
 * @description 针对表【question_submit(判题任务表)】的数据库操作Service实现
 * @createDate 2024-03-09 16:21:50
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {

    @Resource
    private QuestionService questionService;
    @Resource
    private UserService userService;

    @Resource
    @Lazy //懒加载， 只有使用到的时候进行初始化，为了解决循环依赖
    JudgeService judgeService;


    /**
     * 题目提交
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public QuestionSubmitResp doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        // 判断实体是否存在，根据类别获取实体
        Question question = questionService.getById(questionSubmitAddRequest.getQuestionId());
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }
        if (loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }


        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setQuestionId(questionSubmitAddRequest.getQuestionId());
        questionSubmit.setUserId(loginUser.getId());
        questionSubmit.setLanguage(language);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITTING.getValue());
        questionSubmit.setJudgeInfo("{}");

        boolean isSave = this.save(questionSubmit);
        // 是否已题目提交
        if (!isSave) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "问题提交失败");
        }
        Long questionSubmitId = questionSubmit.getId();
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
            judgeService.doJudge(questionSubmitId);
        });
        voidCompletableFuture.join();
        String token = RequestTokenUtils.getRequestToken();
        System.out.println("token: " + token);

        QuestionSubmitResp questionSubmitResp = new QuestionSubmitResp();
        questionSubmitResp.setQuestionSubmitId(questionSubmitId);
        questionSubmitResp.setRefreshToken(token);

        return questionSubmitResp;
    }

    /**
     * 通过请求条件获得querywrapper
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();
        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();


        // 拼接查询条件
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(StringUtils.isNotEmpty(language), "language", language);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        queryWrapper.eq("isDelete", false);

        return queryWrapper;
    }


    /**
     * 单条记录脱敏
     *
     * @param questionSubmit
     * @return
     */
    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, HttpServletRequest request) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
            questionSubmitVO.setCode(null);
        // 1. 关联查询用户信息
        Long userId = questionSubmit.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionSubmitVO.setUserVO(userVO);
        // 2. 关联查询题目信息
        Long questionId = questionSubmit.getQuestionId();
        Question question = null;
        if (questionId != null && questionId > 0) {
            question = questionService.getById(questionId);
        }
        QuestionVO questionVO = questionService.getQuestionVO(question, request);
        questionSubmitVO.setQuestionVO(questionVO);
        return questionSubmitVO;
    }

    /**
     * 多条记录脱敏
     *
     * @param questionSubmitPage
     * @return
     */
    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, HttpServletRequest request) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollUtil.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        // 填充信息
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream().map(questionSubmit -> {
            return getQuestionSubmitVO(questionSubmit,request);
        }).collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }

}




