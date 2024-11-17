package com.qsj.acoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qsj.acoj.common.ErrorCode;
import com.qsj.acoj.exception.BusinessException;
import com.qsj.acoj.mapper.ContestMapper;
import com.qsj.acoj.mapper.ContestParticipantMapper;
import com.qsj.acoj.mapper.ContestProblemMapper;
import com.qsj.acoj.mapper.ContestRankMapper;
import com.qsj.acoj.model.dto.contest.ContestAddRequest;
import com.qsj.acoj.model.dto.contest.ContestProblemRequest;
import com.qsj.acoj.model.dto.contest.ContestQueryRequest;
import com.qsj.acoj.model.dto.contest.ContestUpdateRequest;
import com.qsj.acoj.model.entity.Contest;
import com.qsj.acoj.model.entity.ContestParticipant;
import com.qsj.acoj.model.entity.ContestProblem;
import com.qsj.acoj.model.entity.ContestRank;
import com.qsj.acoj.model.entity.Question;
import com.qsj.acoj.model.enums.ContestStatusEnum;
import com.qsj.acoj.model.vo.ContestVO;
import com.qsj.acoj.model.vo.ContestQuestionVO;
import com.qsj.acoj.service.ContestService;
import com.qsj.acoj.service.QuestionService;
import com.qsj.acoj.service.UserService;
import com.qsj.acoj.model.vo.LoginUserVO;
import com.qsj.acoj.model.vo.UserVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 比赛服务现
 */
@Service
public class ContestServiceImpl extends ServiceImpl<ContestMapper, Contest> implements ContestService {

    @Resource
    private ContestProblemMapper contestProblemMapper;

    @Resource
    private ContestParticipantMapper contestParticipantMapper;

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    private ContestRankMapper contestRankMapper;

    @Override
    public void validContest(Contest contest, boolean add) {
        if (contest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = contest.getName();
        LocalDateTime startTime = contest.getStartTime();
        LocalDateTime endTime = contest.getEndTime();

        // 创建时，参数不能为空
        if (add) {
            if (StringUtils.isAnyBlank(name)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            if (startTime == null || endTime == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            // 结束时间必须大于开始时间
            if (endTime.isBefore(startTime)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "结束时间不能早于开始时间");
            }
        }
        if (StringUtils.isNotBlank(name) && name.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
    }

    @Override
    public QueryWrapper<Contest> getQueryWrapper(ContestQueryRequest contestQueryRequest) {
        QueryWrapper<Contest> queryWrapper = new QueryWrapper<>();
        if (contestQueryRequest == null) {
            return queryWrapper;
        }
        Long id = contestQueryRequest.getId();
        String name = contestQueryRequest.getTitle();
        String status = contestQueryRequest.getStatus();
        LocalDateTime now = LocalDateTime.now();

        queryWrapper.eq(id != null, "id", id);
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        
        // 根据时间判断比赛状态
        if (StringUtils.isNotBlank(status)) {
            switch (status) {
                case "1":  // 未开始
                    queryWrapper.gt("startTime", now);
                    break;
                case "2":  // 进行中
                    queryWrapper.le("startTime", now)
                            .gt("endTime", now);
                    break;
                case "3":  // 已结束
                    queryWrapper.le("endTime", now);
                    break;
                default:
                    break;
            }
        }
        
        queryWrapper.eq("isDelete", 0);
        return queryWrapper;
    }

    @Override
    public ContestVO getContestVO(Contest contest, HttpServletRequest request) {
        ContestVO contestVO = new ContestVO();
        BeanUtils.copyProperties(contest, contestVO);

        // 获取创建者信息
        Long userId = contest.getUserId();
        UserVO userVO = userService.getUserVO(userService.getById(userId));
        contestVO.setUserVO(userVO);

        // 获取比赛题目列表（按题目顺序排序）
        QueryWrapper<ContestProblem> problemQueryWrapper = new QueryWrapper<>();
        problemQueryWrapper.eq("contestId", contest.getId());
        problemQueryWrapper.orderByAsc("problemOrder");
        List<ContestProblem> contestProblems = contestProblemMapper.selectList(problemQueryWrapper);

        // 转换为ContestQuestionVO列表
        List<ContestQuestionVO> problems = contestProblems.stream()
                .map(contestProblem -> {
                    Question question = questionService.getById(contestProblem.getProblemId());
                    ContestQuestionVO questionVO = new ContestQuestionVO();
                    // 复制基础题目信息
                    BeanUtils.copyProperties(questionService.getQuestionVO(question), questionVO);
                    // 设置比赛中的题目序号
                    questionVO.setProblemOrder(contestProblem.getProblemOrder());
                    return questionVO;
                })
                .collect(Collectors.toList());
        contestVO.setProblems(problems);

        // 获取参与人数
        QueryWrapper<ContestParticipant> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("contestId", contest.getId());
        long participantCount = contestParticipantMapper.selectCount(queryWrapper);
        contestVO.setParticipantCount((int) participantCount);

        // 判断当前用户是否已报名
        LoginUserVO loginUser = userService.getLoginUser(request);
        if (loginUser != null) {
            QueryWrapper<ContestParticipant> participantQueryWrapper = new QueryWrapper<>();
            participantQueryWrapper.eq("contestId", contest.getId());
            participantQueryWrapper.eq("userId", loginUser.getId());
            boolean isRegistered = contestParticipantMapper.exists(participantQueryWrapper);
            contestVO.setIsRegistered(isRegistered);
        }

        return contestVO;
    }

    @Override
    public Page<ContestVO> getContestVOPage(Page<Contest> contestPage, HttpServletRequest request) {
        List<Contest> contestList = contestPage.getRecords();
        Page<ContestVO> contestVOPage = new Page<>(contestPage.getCurrent(), contestPage.getSize(), contestPage.getTotal());
        List<ContestVO> contestVOList = contestList.stream()
                .map(contest -> getContestVO(contest, request))
                .collect(Collectors.toList());
        contestVOPage.setRecords(contestVOList);
        return contestVOPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean registerContest(long contestId, long userId) {
        // 校验比赛是否存在
        Contest contest = this.getById(contestId);
        if (contest == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "���赛不存在");
        }

        // 校验比赛状态
        if ("completed".equals(contest.getStatus())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "比赛已结束");
        }
        if ("ongoing".equals(contest.getStatus())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "比赛已开始，无法报名");
        }

        // 校验是否已经报名
        QueryWrapper<ContestParticipant> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("contestId", contestId);
        queryWrapper.eq("userId", userId);
        long count = contestParticipantMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "已经报名");
        }

        // 创建参与记录
        ContestParticipant participant = new ContestParticipant();
        participant.setContestId(contestId);
        participant.setUserId(userId);
        participant.setStatus("REGISTERED");
        return contestParticipantMapper.insert(participant) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addContest(Contest contest, List<ContestProblemRequest> questions) {
        // 1. 校验题目是否都存在
        for (ContestProblemRequest question : questions) {
            Question dbQuestion = questionService.getById(question.getId());
            if (dbQuestion == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在：" + question.getId());
            }
        }

        // 2. 保存比赛基本信息
        boolean saveResult = this.save(contest);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "创建比赛失败");
        }

        // 3. 批量保存比赛题目关联
        List<ContestProblem> contestProblems = questions.stream()
                .map(question -> {
                    ContestProblem contestProblem = new ContestProblem();
                    contestProblem.setContestId(contest.getId());
                    contestProblem.setProblemId(Long.valueOf(question.getId()));
                    contestProblem.setProblemOrder(question.getOrder());
                    return contestProblem;
                })
                .collect(Collectors.toList());

        boolean batchSaveResult = contestProblemMapper.insertBatch(contestProblems);
        if (!batchSaveResult) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "添加比赛题目失败");
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteContest(long contestId) {
        // 1. 删除比赛基本信息
        boolean removeResult = this.removeById(contestId);
        if (!removeResult) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除比赛失败");
        }

        // 2. 删除比赛题目关联
        QueryWrapper<ContestProblem> problemQueryWrapper = new QueryWrapper<>();
        problemQueryWrapper.eq("contestId", contestId);
        boolean removeProblemResult = contestProblemMapper.delete(problemQueryWrapper) >= 0;
        if (!removeProblemResult) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除比赛题目关联失败");
        }

        // 3. 删除比赛参与者记录
        QueryWrapper<ContestParticipant> participantQueryWrapper = new QueryWrapper<>();
        participantQueryWrapper.eq("contestId", contestId);
        boolean removeParticipantResult = contestParticipantMapper.delete(participantQueryWrapper) >= 0;
        if (!removeParticipantResult) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除比赛参与者记录失败");
        }

        // 4. 删除比赛排名记录
        QueryWrapper<ContestRank> rankQueryWrapper = new QueryWrapper<>();
        rankQueryWrapper.eq("contestId", contestId);
        boolean removeRankResult = contestRankMapper.delete(rankQueryWrapper) >= 0;
        if (!removeRankResult) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除比赛排名记录失败");
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean calculateContestRanking(long contestId) {
        // 1. 获取比赛信息
        Contest contest = this.getById(contestId);
        if (contest == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 2. 检查比赛是否已结束
        if (!contest.getEndTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "比赛尚未结束");
        }

        // 3. 获取所有参赛者的成绩，按照新的排名规则排序
        QueryWrapper<ContestRank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("contestId", contestId)
                .orderByDesc("totalScore")    // 首先按总分降序
                .orderByAsc("penaltyTime")    // 同分则按罚时升序
                .orderByAsc("totalTime");     // 罚时相同则按总用时升序
        List<ContestRank> ranks = contestRankMapper.selectList(queryWrapper);

        // 4. 更新排名
        for (int i = 0; i < ranks.size(); i++) {
            ContestRank rank = ranks.get(i);
            rank.setContestRank(i + 1);  // 直接使用索引+1作为排名
            contestRankMapper.updateById(rank);
        }

        // 5. 更新比赛状态为已完成
        contest.setStatus(ContestStatusEnum.COMPLETED.getValue());
        return this.updateById(contest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateContest(ContestUpdateRequest contestUpdateRequest, LoginUserVO loginUser) {
        // 1. 校验比赛是否存在
        Long contestId = contestUpdateRequest.getId();
        Contest oldContest = this.getById(contestId);
        if (oldContest == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 2. 校验权限
        if (!loginUser.getUserRole().equals("admin") && !oldContest.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 3. 校验比赛状态
        if ("ongoing".equals(oldContest.getStatus()) || "completed".equals(oldContest.getStatus())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "进行中或已结束的比赛不能修改");
        }

        // 4. 更新比赛基本信息
        Contest contest = new Contest();
        contest.setId(contestId);
        contest.setName(contestUpdateRequest.getTitle());
        contest.setDescription(contestUpdateRequest.getDescription());
        contest.setStartTime(contestUpdateRequest.getStartTime());
        contest.setEndTime(contestUpdateRequest.getEndTime());

        boolean updateResult = this.updateById(contest);
        if (!updateResult) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新比赛信息失败");
        }

        // 5. 更新题目关联
        List<ContestProblemRequest> questions = contestUpdateRequest.getQuestions();
        if (questions != null && !questions.isEmpty()) {
            // 校验题目ID
            for (ContestProblemRequest question : questions) {
                if (question == null || StringUtils.isBlank(question.getId())) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目信息不完");
                }
                // 校验题目是否存在
                Question dbQuestion = questionService.getById(question.getId());
                if (dbQuestion == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在：" + question.getId());
                }
            }

            // 删除旧的题目关联
            QueryWrapper<ContestProblem> deleteWrapper = new QueryWrapper<>();
            deleteWrapper.eq("contestId", contestId);
            contestProblemMapper.delete(deleteWrapper);

            // 添加新的题目关联
            List<ContestProblem> contestProblems = questions.stream()
                    .map(question -> {
                        ContestProblem contestProblem = new ContestProblem();
                        contestProblem.setContestId(contestId);
                        contestProblem.setProblemId(Long.valueOf(question.getId()));
                        contestProblem.setProblemOrder(question.getOrder());
                        return contestProblem;
                    })
                    .collect(Collectors.toList());

            boolean batchSaveResult = contestProblemMapper.insertBatch(contestProblems);
            if (!batchSaveResult) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新比赛题目失败");
            }
        }

        return true;
    }
} 