package com.qsj.acoj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qsj.acoj.model.dto.contest.ContestQueryRequest;
import com.qsj.acoj.model.entity.Contest;
import com.qsj.acoj.model.dto.contest.ContestProblemRequest;
import com.qsj.acoj.model.vo.ContestVO;
import com.qsj.acoj.model.dto.contest.ContestUpdateRequest;
import com.qsj.acoj.model.vo.LoginUserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 比赛服务
 */
public interface ContestService extends IService<Contest> {

    /**
     * 校验比赛信息
     * @param contest
     * @param add
     */
    void validContest(Contest contest, boolean add);

    /**
     * 获取查询条件
     * @param contestQueryRequest
     * @return
     */
    QueryWrapper<Contest> getQueryWrapper(ContestQueryRequest contestQueryRequest);

    /**
     * 获取比赛封装
     * @param contest
     * @param request
     * @return
     */
    ContestVO getContestVO(Contest contest, HttpServletRequest request);

    /**
     * 分页获取比赛封装
     * @param contestPage
     * @param request
     * @return
     */
    Page<ContestVO> getContestVOPage(Page<Contest> contestPage, HttpServletRequest request);

    /**
     * 报名比赛
     * @param contestId
     * @param userId
     * @return
     */
    boolean registerContest(long contestId, long userId);

    /**
     * 创建比赛
     * @param contest 比赛信息
     * @param questions 题目列表
     * @return
     */
    boolean addContest(Contest contest, List<ContestProblemRequest> questions);

    /**
     * 删除比赛
     * @param contestId 比赛ID
     * @return
     */
    boolean deleteContest(long contestId);

    /**
     * 计算比赛排名
     * @param contestId 比赛ID
     * @return 是否计算成功
     */
    boolean calculateContestRanking(long contestId);

    /**
     * 修改比赛
     * @param contestUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateContest(ContestUpdateRequest contestUpdateRequest, LoginUserVO loginUser);
} 