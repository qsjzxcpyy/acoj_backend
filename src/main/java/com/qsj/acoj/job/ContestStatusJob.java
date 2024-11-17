package com.qsj.acoj.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.qsj.acoj.model.entity.Contest;
import com.qsj.acoj.model.enums.ContestStatusEnum;
import com.qsj.acoj.service.ContestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 比赛状态更新定时任务
 */
@Component
@Slf4j
public class ContestStatusJob {

    @Resource
    private ContestService contestService;

    /**
     * 每天凌晨1点执行一次，更新比赛状态
     * "0 0 1 * * ?" 表示每天凌晨1点执行
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void updateContestStatus() {
        LocalDateTime now = LocalDateTime.now();
        
        // 更新待开始->进行中的比赛
        UpdateWrapper<Contest> startingWrapper = new UpdateWrapper<>();
        startingWrapper.eq("status", ContestStatusEnum.PENDING.getValue())
                .le("startTime", now)
                .gt("endTime", now)
                .set("status", ContestStatusEnum.ONGOING.getValue());
        boolean startResult = contestService.update(startingWrapper);
        if (startResult) {
            log.info("Updated contests from pending to ongoing at {}", now);
        }

        // 更新所有已过结束时间的比赛为已结束状态
        UpdateWrapper<Contest> endingWrapper = new UpdateWrapper<>();
        endingWrapper.ne("status", ContestStatusEnum.COMPLETED.getValue())  // 状态不是已结束
                .le("endTime", now)  // 已过结束时间
                .set("status", ContestStatusEnum.COMPLETED.getValue());
        boolean endResult = contestService.update(endingWrapper);
        if (endResult) {
            // 获取刚刚结束的比赛，计算排名
            QueryWrapper<Contest> justEndedWrapper = new QueryWrapper<>();
            justEndedWrapper.eq("status", ContestStatusEnum.COMPLETED.getValue())
                    .le("endTime", now)
                    .ge("endTime", now.minusDays(1));  // 最近一天内结束的比赛
            List<Contest> justEndedContests = contestService.list(justEndedWrapper);
            
            // 计算每个刚结束比赛的排名
            for (Contest contest : justEndedContests) {
                try {
                    contestService.calculateContestRanking(contest.getId());
                    log.info("Calculated rankings for contest: {} at {}", contest.getId(), now);
                } catch (Exception e) {
                    log.error("Failed to calculate rankings for contest: {}", contest.getId(), e);
                }
            }
        }
    }
} 