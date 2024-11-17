package com.qsj.acoj.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qsj.acoj.common.BaseResponse;
import com.qsj.acoj.common.ErrorCode;
import com.qsj.acoj.common.ResultUtils;
import com.qsj.acoj.exception.BusinessException;
import com.qsj.acoj.judge.codesandbox.model.JudgeInfo;
import com.qsj.acoj.mapper.*;
import com.qsj.acoj.model.dto.contest.ContestAddRequest;
import com.qsj.acoj.model.dto.contest.ContestProblemRequest;
import com.qsj.acoj.model.dto.contest.ContestQueryRequest;
import com.qsj.acoj.model.dto.contest.ContestUpdateRequest;
import com.qsj.acoj.model.dto.question.QuestionSubmitResp;
import com.qsj.acoj.model.entity.*;
import com.qsj.acoj.model.enums.ContestStatusEnum;
import com.qsj.acoj.model.enums.JudgeInfoMessageEnum;
import com.qsj.acoj.model.enums.QuestionSubmitStatusEnum;
import com.qsj.acoj.model.vo.*;
import com.qsj.acoj.service.ContestService;
import com.qsj.acoj.service.QuestionService;
import com.qsj.acoj.service.QuestionSubmitService;
import com.qsj.acoj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 比赛控制器
 */
@RestController
@RequestMapping("/contest")
@Slf4j
public class ContestController {

    @Resource
    private ContestService contestService;

    @Resource
    private UserService userService;

    @Resource
    private ContestParticipantMapper contestParticipantMapper;

    @Resource
    private ContestProblemMapper contestProblemMapper;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private ContestRankMapper contestRankMapper;

    @Resource
    private ContestSubmissionMapper contestSubmissionMapper;

    @Resource
    private QuestionService questionService;


    /**
     * 创建比赛
     *
     * @param contestAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addContest(@RequestBody ContestAddRequest contestAddRequest, HttpServletRequest request) {
        if (contestAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 1. 校验请求参数
        if (StringUtils.isBlank(contestAddRequest.getTitle())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "比赛名称不能为空");
        }
        if (contestAddRequest.getStartTime() == null || contestAddRequest.getEndTime() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "比赛时间不能为空");
        }
        if (contestAddRequest.getEndTime().isBefore(contestAddRequest.getStartTime())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "结束时间不能早于开始时间");
        }
        List<ContestProblemRequest> questions = contestAddRequest.getQuestions();
        if (questions == null || questions.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目列表不能为空");
        }

        // 2. 获取当前登录用户
        LoginUserVO loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        // 3. 创建比赛对象
        Contest contest = new Contest();
        contest.setName(contestAddRequest.getTitle());
        contest.setDescription(contestAddRequest.getDescription());
        contest.setStartTime(contestAddRequest.getStartTime());
        contest.setEndTime(contestAddRequest.getEndTime());
        contest.setUserId(loginUser.getId());
        contest.setStatus(ContestStatusEnum.PENDING.getValue());

        // 4. 创建比赛并关联题目
        boolean result = contestService.addContest(contest, questions);

        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(contest.getId());
    }

    /**
     * 获取比赛列表
     *
     * @param contestQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<ContestVO>> listContestByPage(@RequestBody(required = false) ContestQueryRequest contestQueryRequest,
                                                           HttpServletRequest request) {
        if (contestQueryRequest == null) {
            contestQueryRequest = new ContestQueryRequest();
            contestQueryRequest.setCurrent(1);
            contestQueryRequest.setPageSize(10);
        }
        long current = contestQueryRequest.getCurrent();
        long size = contestQueryRequest.getPageSize();
        Page<Contest> contestPage = contestService.page(new Page<>(current, size),
                contestService.getQueryWrapper(contestQueryRequest));
        return ResultUtils.success(contestService.getContestVOPage(contestPage, request));
    }

    /**
     * 获取比赛信息
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<ContestVO> getContestById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Contest contest = contestService.getById(id);
        if (contest == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(contestService.getContestVO(contest, request));
    }

    /**
     * 报名比赛
     *
     * @param id
     * @param request
     * @return
     */
    @PostMapping("/register/{id}")
    public BaseResponse<Boolean> registerContest(@PathVariable("id") long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUserVO loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        boolean result = contestService.registerContest(id, loginUser.getId());
        return ResultUtils.success(result);
    }

    /**
     * 删除比赛
     *
     * @param id
     * @param request
     * @return
     */
    @PostMapping("/delete/{id}")
    public BaseResponse<Boolean> deleteContest(@PathVariable("id") long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取当前登录用户
        LoginUserVO loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 判断是否是管理员或者比赛创建者
        Contest contest = contestService.getById(id);
        if (contest == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (!loginUser.getUserRole().equals("admin") && !contest.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限删除该比赛");
        }
        boolean result = contestService.deleteContest(id);
        return ResultUtils.success(result);
    }

    /**
     * 修改比赛
     *
     * @param contestUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateContest(@RequestBody ContestUpdateRequest contestUpdateRequest,
                                               HttpServletRequest request) {
        if (contestUpdateRequest == null || contestUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 校验比赛时间
        if (contestUpdateRequest.getEndTime().isBefore(contestUpdateRequest.getStartTime())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "结束时间不能早于开始时间");
        }

        // 校验题目列表
        List<ContestProblemRequest> questions = contestUpdateRequest.getQuestions();
        if (questions == null || questions.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目列表不能为空");
        }

        // 获取当前登录用户
        LoginUserVO loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        boolean result = contestService.updateContest(contestUpdateRequest, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 获取用户在比赛中的提交记录
     *
     * @param contestId
     * @param request
     * @return
     */
    @GetMapping("/user/submissions/{contestId}")
    public BaseResponse<List<ContestQuestionSubmitVO>> getUserContestSubmissions(@PathVariable("contestId") long contestId,
                                                                                 HttpServletRequest request) {
        if (contestId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取当前登录用户
        LoginUserVO loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        // 检查比赛是否存在
        Contest contest = contestService.getById(contestId);
        if (contest == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "比赛不存在");
        }

        // 检查用户是否报名
        QueryWrapper<ContestParticipant> participantQueryWrapper = new QueryWrapper<>();
        participantQueryWrapper.eq("contestId", contestId);
        participantQueryWrapper.eq("userId", loginUser.getId());
        if (!contestParticipantMapper.exists(participantQueryWrapper)) {
            return ResultUtils.success(null);
        }

        // 获取比赛题目列表
        QueryWrapper<ContestProblem> problemQueryWrapper = new QueryWrapper<>();
        problemQueryWrapper.eq("contestId", contestId);
        problemQueryWrapper.orderByAsc("problemOrder");
        List<ContestProblem> contestProblems = contestProblemMapper.selectList(problemQueryWrapper);

        // 获取用户在比赛中的所有提交记录
        List<ContestQuestionSubmitVO> resultList = contestProblems.stream()
                .map(contestProblem -> {
                    ContestQuestionSubmitVO vo = new ContestQuestionSubmitVO();
                    vo.setQuestionId(contestProblem.getProblemId());
                    vo.setProblemOrder(contestProblem.getProblemOrder());

                    // 获取该题目在此次比赛中的提交记录
                    QueryWrapper<ContestSubmission> submitQueryWrapper = new QueryWrapper<>();
                    submitQueryWrapper.eq("contestId", contestId)
                            .eq("problemId", contestProblem.getProblemId());
                    List<ContestSubmission> submissions = contestSubmissionMapper.selectList(submitQueryWrapper);

                    // 过滤出当前用户的提交记录
                    submissions = submissions.stream()
                            .filter(submission -> {
                                QuestionSubmit questionSubmit = questionSubmitService.getById(submission.getSubmissionId());
                                return questionSubmit.getUserId().equals(loginUser.getId());
                            })
                            .collect(Collectors.toList());

                    // 设置提交次数
                    vo.setSubmitCount(submissions.size());

                    // 检查是否有通过的提交
                    boolean hasAccepted = submissions.stream()
                            .anyMatch(submission -> {
                                QuestionSubmit questionSubmit = questionSubmitService.getById(submission.getSubmissionId());
                                String judgeInfoStr = questionSubmit.getJudgeInfo();
                                JudgeInfo judgeInfo = JSONUtil.toBean(judgeInfoStr, JudgeInfo.class);
                                return judgeInfo.getMessage().equals(JudgeInfoMessageEnum.ACCEPTED.getText());
                            });
                    vo.setAccepted(hasAccepted);

                    return vo;
                })
                .collect(Collectors.toList());

        return ResultUtils.success(resultList);
    }

    /**
     * 获取比赛排名详情
     *
     * @param contestId
     * @param current 当前页码
     * @param pageSize 每页大小
     * @param request
     * @return
     */
    @GetMapping("/rank/detail/{contestId}")
    public BaseResponse<Page<ContestRankDetailVO>> getContestRankDetail(
            @PathVariable("contestId") long contestId,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long pageSize,
            HttpServletRequest request) {
        if (contestId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 检查比赛是否存在
        Contest contest = contestService.getById(contestId);
        if (contest == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "比赛不存在");
        }

        // 获取所有参赛者的排名记录（分页）
        QueryWrapper<ContestRank> rankQueryWrapper = new QueryWrapper<>();
        rankQueryWrapper.eq("contestId", contestId)
                .orderByDesc("totalScore")
                .orderByAsc("penaltyTime")
                .orderByAsc("totalTime");
        Page<ContestRank> rankPage = contestRankMapper.selectPage(new Page<>(current, pageSize), rankQueryWrapper);

        // 获取比赛题目列表
        QueryWrapper<ContestProblem> problemQueryWrapper = new QueryWrapper<>();
        problemQueryWrapper.eq("contestId", contestId)
                .orderByAsc("problemOrder");
        List<ContestProblem> problems = contestProblemMapper.selectList(problemQueryWrapper);

        // 转换为详细排名信息
        List<ContestRankDetailVO> rankDetails = rankPage.getRecords().stream()
                .map(rank -> {
                    ContestRankDetailVO detailVO = new ContestRankDetailVO();
                    detailVO.setUserId(rank.getParticipantId());
                    detailVO.setTotalScore(rank.getTotalScore());
                    detailVO.setTotalTime(rank.getTotalTime());
                    detailVO.setPenaltyTime(rank.getPenaltyTime());
                    detailVO.setSolvedProblems(rank.getSolvedProblems());
                    detailVO.setRank(rank.getContestRank());

                    // 获取用户信息
                    User user = userService.getById(rank.getParticipantId());
                    detailVO.setUserVO(userService.getUserVO(user));

                    // 获取每���题的提交情况
                    List<ContestProblemDetailVO> problemDetails = problems.stream()
                            .map(problem -> {
                                ContestProblemDetailVO detail = new ContestProblemDetailVO();
                                detail.setProblemId(problem.getProblemId());
                                detail.setProblemOrder(problem.getProblemOrder());
                                
                                // 获取题目信息
                                Question questionInfo = questionService.getById(problem.getProblemId());
                                detail.setProblemTitle(questionInfo.getTitle());

                                // 获取该用户对这道题的所有提交
                                QueryWrapper<ContestSubmission> submitQueryWrapper = new QueryWrapper<>();
                                submitQueryWrapper.eq("contestId", contestId)
                                        .eq("problemId", problem.getProblemId());
                                List<ContestSubmission> submissions = contestSubmissionMapper.selectList(submitQueryWrapper);

                                submissions = submissions.stream().filter(contestSubmission -> {
                                          QuestionSubmit questionSubmit = questionSubmitService.getById(contestSubmission.getSubmissionId());
                                          User user1 = userService.getById(questionSubmit.getUserId());
                                          return user1.getId().equals(rank.getParticipantId());
                                        }
                                        ).collect(Collectors.toList());

                                // 统计提交情况
                                detail.setSubmitCount(submissions.size());
                                detail.setAccepted(false);
                                detail.setPenaltyTime(0);

                                // 找出第一次通过的提交
                                for (ContestSubmission submission : submissions) {
                                    QuestionSubmit questionSubmit = questionSubmitService.getById(submission.getSubmissionId());
                                    String judgeInfoStr = questionSubmit.getJudgeInfo();
                                    JudgeInfo judgeInfo = JSONUtil.toBean(judgeInfoStr, JudgeInfo.class);
                                    
                                    if (judgeInfo.getMessage().equals(JudgeInfoMessageEnum.ACCEPTED.getText())) {
                                        detail.setAccepted(true);
                                        // 计算从比赛开始到提交的分钟数
                                        long minutes = Duration.between(contest.getStartTime(), 
                                                questionSubmit.getCreateTime().toInstant()
                                                        .atZone(ZoneId.systemDefault())
                                                        .toLocalDateTime())
                                                .toMinutes();
                                        detail.setFirstAcceptedTime((int) minutes);
                                        break;
                                    }
                                }

                                // 如果有提交但未通过，计算罚时
                                if (!detail.getAccepted() && detail.getSubmitCount() > 0) {
                                    detail.setPenaltyTime(detail.getSubmitCount() * 5);
                                }

                                return detail;
                            })
                            .collect(Collectors.toList());
                    detailVO.setProblemDetails(problemDetails);

                    return detailVO;
                })
                .collect(Collectors.toList());

        // 创建分页结果
        Page<ContestRankDetailVO> result = new Page<>(current, pageSize, rankPage.getTotal());
        result.setRecords(rankDetails);

        return ResultUtils.success(result);
    }

    /**
     * 获取比赛题目统计信息
     *
     * @param contestId
     * @param request
     * @return
     */
    @GetMapping("/problem/statistics/{contestId}")
    public BaseResponse<List<ContestProblemStatisticsVO>> getContestProblemStatistics(@PathVariable("contestId") long contestId,
                                                                                      HttpServletRequest request) {
        if (contestId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 检查比赛是否存在
        Contest contest = contestService.getById(contestId);
        if (contest == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "比赛不存在");
        }

        // 获取比赛题目列表
        QueryWrapper<ContestProblem> problemQueryWrapper = new QueryWrapper<>();
        problemQueryWrapper.eq("contestId", contestId)
                .orderByAsc("problemOrder");
        List<ContestProblem> problems = contestProblemMapper.selectList(problemQueryWrapper);

        // 统计每道题目的提交情况
        List<ContestProblemStatisticsVO> statistics = problems.stream()
                .map(problem -> {
                    ContestProblemStatisticsVO vo = new ContestProblemStatisticsVO();
                    vo.setProblemId(problem.getProblemId());
                    vo.setProblemOrder(problem.getProblemOrder());

                    // 获取题目信息
                    Question questionInfo = questionService.getById(problem.getProblemId());
                    vo.setProblemTitle(questionInfo.getTitle());

                    // 获取该题目在比赛中的所有提交
                    QueryWrapper<ContestSubmission> submitQueryWrapper = new QueryWrapper<>();
                    submitQueryWrapper.eq("contestId", contestId)
                            .eq("problemId", problem.getProblemId());
                    List<ContestSubmission> submissions = contestSubmissionMapper.selectList(submitQueryWrapper);

                    // 统计总提交数
                    vo.setTotalSubmissions(submissions.size());

                    // 统计通过数
                    int acceptedCount = 0;
                    for (ContestSubmission submission : submissions) {
                        QuestionSubmit questionSubmit = questionSubmitService.getById(submission.getSubmissionId());
                        String judgeInfoStr = questionSubmit.getJudgeInfo();
                        JudgeInfo judgeInfo = JSONUtil.toBean(judgeInfoStr, JudgeInfo.class);
                        if (judgeInfo.getMessage().equals(JudgeInfoMessageEnum.ACCEPTED.getText())) {
                            acceptedCount++;
                        }
                    }
                    vo.setAcceptedCount(acceptedCount);

                    // 计算通过率
                    double acceptedRate = vo.getTotalSubmissions() > 0
                            ? (double) acceptedCount / vo.getTotalSubmissions() * 100
                            : 0.0;
                    vo.setAcceptedRate(Math.round(acceptedRate * 100.0) / 100.0);  // 保留两位小数

                    return vo;
                })
                .collect(Collectors.toList());

        return ResultUtils.success(statistics);
    }

    /**
     * 获取当前用户在比赛中的排名
     *
     * @param contestId
     * @param request
     * @return
     */
    @GetMapping("/rank/my/{contestId}")
    public BaseResponse<ContestRankDetailVO> getMyContestRank(@PathVariable("contestId") long contestId,
            HttpServletRequest request) {
        if (contestId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取当前登录用户
        LoginUserVO loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        // ��查比赛是否存在
        Contest contest = contestService.getById(contestId);
        if (contest == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "比赛不存在");
        }

        // 检查用户是否报名
        QueryWrapper<ContestParticipant> participantQueryWrapper = new QueryWrapper<>();
        participantQueryWrapper.eq("contestId", contestId);
        participantQueryWrapper.eq("userId", loginUser.getId());
        if (!contestParticipantMapper.exists(participantQueryWrapper)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未报名该比赛");
        }

        // 获取当前用户的排名记录
        QueryWrapper<ContestRank> rankQueryWrapper = new QueryWrapper<>();
        rankQueryWrapper.eq("contestId", contestId)
                .eq("participantId", loginUser.getId());
        ContestRank myRank = contestRankMapper.selectOne(rankQueryWrapper);

        if (myRank == null) {
            // 如果还没有排名记录，返回初始状态
            ContestRankDetailVO detailVO = new ContestRankDetailVO();
            detailVO.setUserId(loginUser.getId());
            detailVO.setTotalScore(0);
            detailVO.setTotalTime(0);
            detailVO.setPenaltyTime(0);
            detailVO.setSolvedProblems(0);
            detailVO.setRank(0);  // 0表示未排名
            return ResultUtils.success(detailVO);
        }

        // 获取所有参赛者的排名记录（按排名规则排序）
        QueryWrapper<ContestRank> allRankQueryWrapper = new QueryWrapper<>();
        allRankQueryWrapper.eq("contestId", contestId)
                .orderByDesc("totalScore")
                .orderByAsc("totalTime")
                .orderByAsc("penaltyTime");
        List<ContestRank> allRanks = contestRankMapper.selectList(allRankQueryWrapper);

        // 找到当前用户的排名
        int currentRank = 0;
        for (int i = 0; i < allRanks.size(); i++) {
            if (allRanks.get(i).getParticipantId().equals(loginUser.getId())) {
                currentRank = i + 1;
                break;
            }
        }

        // 构建排名详情
        ContestRankDetailVO detailVO = new ContestRankDetailVO();
        detailVO.setUserId(myRank.getParticipantId());
        detailVO.setTotalScore(myRank.getTotalScore());
        detailVO.setTotalTime(myRank.getTotalTime());
        detailVO.setPenaltyTime(myRank.getPenaltyTime());
        detailVO.setSolvedProblems(myRank.getSolvedProblems());
        detailVO.setRank(currentRank);

        return ResultUtils.success(detailVO);
    }

    /**
     * 检查题目是否在进行中的比赛中
     *
     * @param questionId
     * @return
     */
    @GetMapping("/check/question/{questionId}")
    public BaseResponse<Boolean> checkQuestionInOngoingContest(@PathVariable("questionId") long questionId) {
        if (questionId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取所有进行中的比赛（通过时间判断）
        LocalDateTime now = LocalDateTime.now();
        QueryWrapper<Contest> contestQueryWrapper = new QueryWrapper<>();
        contestQueryWrapper.le("startTime", now)  // 开始时间小于等于当前时间
                .gt("endTime", now)  // 结束时间大于当前时间
                .eq("isDelete", false);
        List<Contest> ongoingContests = contestService.list(contestQueryWrapper);

        if (ongoingContests.isEmpty()) {
            return ResultUtils.success(false);
        }

        // 检查题目是否在任何进行中的比赛中
        List<Long> contestIds = ongoingContests.stream()
                .map(Contest::getId)
                .collect(Collectors.toList());

        QueryWrapper<ContestProblem> problemQueryWrapper = new QueryWrapper<>();
        problemQueryWrapper.eq("problemId", questionId)
                .in("contestId", contestIds);

        boolean exists = contestProblemMapper.exists(problemQueryWrapper);
        return ResultUtils.success(exists);
    }
}