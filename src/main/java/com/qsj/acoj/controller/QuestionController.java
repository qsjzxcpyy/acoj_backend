package com.qsj.acoj.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qsj.acoj.annotation.AuthCheck;
import com.qsj.acoj.common.BaseResponse;
import com.qsj.acoj.common.DeleteRequest;
import com.qsj.acoj.common.ErrorCode;
import com.qsj.acoj.common.ResultUtils;
import com.qsj.acoj.constant.QuestionConstant;
import com.qsj.acoj.constant.UserConstant;
import com.qsj.acoj.convert.QuestionConvert;
import com.qsj.acoj.exception.BusinessException;
import com.qsj.acoj.exception.ThrowUtils;
import com.qsj.acoj.judge.codesandbox.model.JudgeInfo;
import com.qsj.acoj.mapper.ContestParticipantMapper;
import com.qsj.acoj.mapper.ContestProblemMapper;
import com.qsj.acoj.mapper.ContestRankMapper;
import com.qsj.acoj.mapper.ContestSubmissionMapper;
import com.qsj.acoj.model.dto.question.*;
import com.qsj.acoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.qsj.acoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.qsj.acoj.model.entity.*;
import com.qsj.acoj.model.enums.JudgeInfoMessageEnum;
import com.qsj.acoj.model.enums.UserRoleEnum;
import com.qsj.acoj.model.vo.AiChatRecordVo;
import com.qsj.acoj.model.vo.LoginUserVO;
import com.qsj.acoj.model.vo.QuestionSubmitVO;
import com.qsj.acoj.model.vo.QuestionVO;
import com.qsj.acoj.service.*;
import com.qsj.acoj.utils.RequestTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 题目接口
 */
@RestController
@RequestMapping("/question")
@Slf4j
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;
    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private AiQuestionChatService aiQuestionChatService;

    @Resource
    private ContestService contestService;

    @Resource
    private ContestParticipantMapper contestParticipantMapper;

    @Resource
    private ContestProblemMapper contestProblemMapper;

    @Resource
    private ContestSubmissionMapper contestSubmissionMapper;

    @Resource
    private ContestRankMapper contestRankMapper;

    // region 增删改查

    /**
     * 创建
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        if (questionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        List<String> tags = questionAddRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        if (questionAddRequest.getJudgeCase() != null) {
            question.setJudgeCase(JSONUtil.toJsonStr(questionAddRequest.getJudgeCase()));
        }
        if (questionAddRequest.getJudgeConfig() != null) {
            question.setJudgeConfig(JSONUtil.toJsonStr(questionAddRequest.getJudgeConfig()));
        }
        questionService.validQuestion(question, true);
        LoginUserVO loginUser = userService.getLoginUser(request);
        question.setUserId(loginUser.getId());
        question.setFavourNum(0);
        question.setThumbNum(0);
        boolean result = questionService.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newQuestionId = question.getId();
        return ResultUtils.success(newQuestionId);
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUserVO user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestion.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = questionService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        List<String> tags = questionUpdateRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        if (questionUpdateRequest.getJudgeCase() != null) {
            question.setJudgeCase(JSONUtil.toJsonStr(questionUpdateRequest.getJudgeCase()));
        }
        if (questionUpdateRequest.getJudgeConfig() != null) {
            question.setJudgeConfig(JSONUtil.toJsonStr(questionUpdateRequest.getJudgeConfig()));
        }
        // 参数校验

        questionService.validQuestion(question, false);
        long id = questionUpdateRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = questionService.updateById(question);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取question
     */
    @GetMapping("/get")
    public BaseResponse<Question> getQuestionById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        //权限校验，非本人和管理员不能获取到完整的题目信息
        LoginUserVO loginUser = userService.getLoginUser(request);
        if (!(question.getUserId().equals(loginUser.getId())) && !userService.isAdmin(request)
                && !(loginUser.getUserRole().equals(UserRoleEnum.SUPER_ADMIN.getValue()))) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success(question);
    }

    /**
     * 根据 id 获取脱敏question
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionVO> getQuestionVOById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(questionService.getQuestionVO(question));
    }

    /**
     * 分页获取列表（仅管理员）
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Question>> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                           HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionPage);
    }


    /**
     * 分页获取列表（封装类）
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                               HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listMyQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                                 HttpServletRequest request) {
        if (questionQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUserVO loginUser = userService.getLoginUser(request);
        questionQueryRequest.setUserId(loginUser.getId());
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    // endregion


    /**
     * 编辑（题目）
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest, HttpServletRequest request) {
        if (questionEditRequest == null || questionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionEditRequest, question);
        List<String> tags = questionEditRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        if (questionEditRequest.getJudgeCase() != null) {
            question.setJudgeCase(JSONUtil.toJsonStr(questionEditRequest.getJudgeCase()));
        }
        if (questionEditRequest.getJudgeConfig() != null) {
            question.setJudgeConfig(JSONUtil.toJsonStr(questionEditRequest.getJudgeConfig()));
        }
        // 参数校验
        questionService.validQuestion(question, false);
        LoginUserVO loginUser = userService.getLoginUser(request);
        long id = questionEditRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldQuestion.getUserId().equals(loginUser.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = questionService.updateById(question);
        return ResultUtils.success(result);
    }


    /**
     * 题目提交
     *
     * @param questionSubmitAddRequest
     * @param contestId                比赛ID（可选）
     * @param request
     * @return
     */
    @PostMapping("/submit")
    public BaseResponse<QuestionSubmitResp> doQuestionSubmit(
            @RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
            @RequestParam(required = false) Long contestId,
            HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能提交
        final LoginUserVO loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        if (questionSubmitAddRequest.getCode() == null || questionSubmitAddRequest.getCode().trim().length() == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "代码不能为空");
        }

        // 如果是比赛提交，进行比赛相关检查
        if (contestId != null && contestId > 0) {
            Contest contest = contestService.getById(contestId);
            if (contest == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "比赛不存在");
            }
            // 检查比赛时间
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(contest.getStartTime())) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "比赛尚未开始");
            }
            if (now.isAfter(contest.getEndTime())) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "比赛已结束");
            }
            // 检查是否已报名
            QueryWrapper<ContestParticipant> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("contestId", contestId);
            queryWrapper.eq("userId", loginUser.getId());
            if (!contestParticipantMapper.exists(queryWrapper)) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "未报名比赛");
            }
            // 检查题目是否属于该比赛
            QueryWrapper<ContestProblem> problemQueryWrapper = new QueryWrapper<>();
            problemQueryWrapper.eq("contestId", contestId);
            problemQueryWrapper.eq("problemId", questionSubmitAddRequest.getQuestionId());
            if (!contestProblemMapper.exists(problemQueryWrapper)) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "该题目不属于当前比赛");
            }
        }

        QuestionSubmitResp result = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);

        // 如果是比赛提交，更新比赛排名
        if (contestId != null && contestId > 0) {
            // 使用ScheduledExecutorService延迟1.5秒后执行
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.schedule(() -> {
                try {
                    // 获取判题结果
                    QuestionSubmit questionSubmit = questionSubmitService.getById(result.getQuestionSubmitId());
                    String judgeInfoStr = questionSubmit.getJudgeInfo();
                    JudgeInfo judgeInfo = JSONUtil.toBean(judgeInfoStr, JudgeInfo.class);

                    // 更新比赛排名表
                    QueryWrapper<ContestRank> rankQueryWrapper = new QueryWrapper<>();
                    rankQueryWrapper.eq("contestId", contestId)
                            .eq("participantId", loginUser.getId());
                    ContestRank contestRank = contestRankMapper.selectOne(rankQueryWrapper);

                    if (contestRank == null) {
                        // 第一次提交，创建排名记录
                        contestRank = new ContestRank();
                        contestRank.setContestId(contestId);
                        contestRank.setParticipantId(loginUser.getId());
                        contestRank.setTotalScore(0);
                        contestRank.setTotalTime(0);
                        contestRank.setPenaltyTime(0);
                        contestRank.setSolvedProblems(0);
                    }

                    // 判断是否通过
                    if (judgeInfo.getMessage().equals(JudgeInfoMessageEnum.ACCEPTED.getText())) {
                        // 查询该题目在本次比赛中的提交记录
                        QueryWrapper<ContestSubmission> contestSubmissionQueryWrapper = new QueryWrapper<>();
                        contestSubmissionQueryWrapper.eq("contestId", contestId)
                                .eq("problemId", questionSubmitAddRequest.getQuestionId());
                        List<ContestSubmission> contestSubmissions = contestSubmissionMapper.selectList(contestSubmissionQueryWrapper);

                        boolean isFirstAccepted = true;
                        for (ContestSubmission submission : contestSubmissions) {
                            QuestionSubmit prevSubmit = questionSubmitService.getById(submission.getSubmissionId());
                            String prevJudgeInfoStr = prevSubmit.getJudgeInfo();
                            JudgeInfo prevJudgeInfo = JSONUtil.toBean(prevJudgeInfoStr, JudgeInfo.class);
                            if (prevJudgeInfo.getMessage().equals(JudgeInfoMessageEnum.ACCEPTED.getText()) &&
                                    prevSubmit.getUserId().equals(loginUser.getId())) {
                                isFirstAccepted = false;
                                break;
                            }
                        }

                        if (isFirstAccepted) {  // 首次通过
                            contestRank.setSolvedProblems(contestRank.getSolvedProblems() + 1);
                            // 计算本题提交时间（从比赛开始到现在的分钟数）
                            Contest contest = contestService.getById(contestId);
                            long submissionTimeMinutes = Duration.between(contest.getStartTime(), LocalDateTime.now()).toMinutes();
                            // 累加到总用时
                            contestRank.setTotalTime(contestRank.getTotalTime() + (int) submissionTimeMinutes);
                            // 更新总分（每道题1分）
                            contestRank.setTotalScore(contestRank.getTotalScore() + 1);
                        }
                    } else {
                        // 未通过，检查是否已有通过记录
                        QueryWrapper<ContestSubmission> contestSubmissionQueryWrapper = new QueryWrapper<>();
                        contestSubmissionQueryWrapper.eq("contestId", contestId)
                                .eq("problemId", questionSubmitAddRequest.getQuestionId())
                                .eq("userId", loginUser.getId());
                        List<ContestSubmission> contestSubmissions = contestSubmissionMapper.selectList(contestSubmissionQueryWrapper);

                        boolean hasAccepted = contestSubmissions.stream()
                                .anyMatch(submission -> {
                                    QuestionSubmit prevSubmit = questionSubmitService.getById(submission.getSubmissionId());
                                    String prevJudgeInfoStr = prevSubmit.getJudgeInfo();
                                    JudgeInfo prevJudgeInfo = JSONUtil.toBean(prevJudgeInfoStr, JudgeInfo.class);
                                    return prevJudgeInfo.getMessage().equals(JudgeInfoMessageEnum.ACCEPTED.getText());
                                });

                        // 只有在未通过且没有成功记录时才增加罚时
                        if (!hasAccepted) {
                            contestRank.setPenaltyTime(contestRank.getPenaltyTime() + 5);
                        }
                    }

                    // 保存或更新排名记录
                    if (contestRank.getId() == null) {
                        contestRankMapper.insert(contestRank);
                    } else {
                        contestRankMapper.updateById(contestRank);
                    }

                    // 记录提交关系
                    ContestSubmission contestSubmission = new ContestSubmission();
                    contestSubmission.setSubmissionId(result.getQuestionSubmitId());
                    contestSubmission.setContestId(contestId);
                    contestSubmission.setProblemId(questionSubmitAddRequest.getQuestionId());
                    contestSubmission.setUserId(loginUser.getId());
                    contestSubmissionMapper.insert(contestSubmission);
                } finally {
                    executor.shutdown();
                }
            }, 1000, TimeUnit.MILLISECONDS);
        }

        return ResultUtils.success(result);
    }

    /**
     * 分页获取提交题目状态，
     */
    @PostMapping("/question_submit/list/page")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
                                                                         HttpServletRequest request) {
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage, request));
    }

    @GetMapping("/get/requestToken")
    public BaseResponse<String> getRequestToken() {
        String token = RequestTokenUtils.getRequestToken();
        System.out.println("前端调用token生成接口生成的token: " + token);
        return ResultUtils.success(token);
    }

    @PostMapping("/get/chat/response")
    public BaseResponse<String> getChatResponse(@NotEmpty @RequestBody String question, HttpServletRequest request) throws IOException {
        return ResultUtils.success(aiQuestionChatService.getChatResponse(question, request));
    }

    @GetMapping("/get/chat/record")
    public BaseResponse<List<AiChatRecordVo>> getChatRecord(HttpServletRequest request) {
        List<AiQuestionChat> aiQuestionChats = aiQuestionChatService.listTopQuestionChat(request);
        List<AiChatRecordVo> recordVos = QuestionConvert.INSTANCE.mapTo(aiQuestionChats);
        System.out.println("recordVos = " + recordVos);
        return ResultUtils.success(recordVos);
    }

}
