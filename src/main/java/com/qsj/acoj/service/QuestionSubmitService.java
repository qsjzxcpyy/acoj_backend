package com.qsj.acoj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qsj.acoj.model.dto.question.QuestionQueryRequest;
import com.qsj.acoj.model.dto.question.QuestionSubmitResp;
import com.qsj.acoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.qsj.acoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.qsj.acoj.model.entity.Question;
import com.qsj.acoj.model.entity.QuestionSubmit;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qsj.acoj.model.entity.User;
import com.qsj.acoj.model.vo.LoginUserVO;
import com.qsj.acoj.model.vo.QuestionSubmitVO;
import com.qsj.acoj.model.vo.QuestionVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* @author qsj
* @description 针对表【question_submit(判题任务表)】的数据库操作Service
* @createDate 2024-03-09 16:21:50
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {
    /**
     * 点赞
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    QuestionSubmitResp doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, LoginUserVO loginUser);
    /**
     * 获取查询条件
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);


    /**
     * 获取题目封装
     *
     * @param questionSubmit
     * @param request
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit,HttpServletRequest request);

    /**
     * 分页获取题目封装
     *
     * @param questionSubmitPage
     * @param request
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, HttpServletRequest request);

}
