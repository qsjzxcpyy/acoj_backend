package com.qsj.acoj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qsj.acoj.common.BaseResponse;
import com.qsj.acoj.common.ErrorCode;
import com.qsj.acoj.common.ResultUtils;
import com.qsj.acoj.exception.BusinessException;
import com.qsj.acoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.qsj.acoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.qsj.acoj.model.entity.QuestionSubmit;
import com.qsj.acoj.model.entity.User;
import com.qsj.acoj.model.vo.QuestionSubmitVO;
import com.qsj.acoj.service.QuestionSubmitService;
import com.qsj.acoj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 题目提交接口
 *
 *
 */
@RestController
@RequestMapping("/question_submit")
@Slf4j
public class QuestionSubmitController {

//    @Resource
//    private QuestionSubmitService questionSubmitService;
//
//    @Resource
//    private UserService userService;
//
//    /**
//     * 题目提交
//     *
//     * @param questionSubmitAddRequest
//     * @param request
//     * @return resultNum 本次提交变化数
//     */
//    @PostMapping("/")
//    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
//            HttpServletRequest request) {
//        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        // 登录才能提交
//        final User loginUser = userService.getLoginUser(request);
//        long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
//        return ResultUtils.success(questionSubmitId);
//    }
//    /**
//     * 分页获取提交题目状态，
//     *
//     * @param questionSubmitQueryRequest
//     * @return
//     */
//    @PostMapping("/list/page")
//    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
//                                                                         HttpServletRequest request) {
//        long current = questionSubmitQueryRequest.getCurrent();
//        long size = questionSubmitQueryRequest.getPageSize();
//        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
//                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
//        User loginUser = userService.getLoginUser(request);
//        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage,loginUser ,request));
//    }

}
