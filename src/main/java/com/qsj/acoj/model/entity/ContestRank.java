package com.qsj.acoj.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName(value = "contest_rank")
@Data
public class ContestRank implements Serializable {
    /**
     * 自增id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 比赛ID
     */
    private Long contestId;

    /**
     * 参与者ID
     */
    private Long participantId;

    /**
     * 总得分
     */
    private Integer totalScore;

    /**
     * 总时间（分钟）
     */
    private Integer totalTime;

    /**
     * 总罚时（分钟）
     */
    private Integer penaltyTime;

    /**
     * 解决的题目数
     */
    private Integer solvedProblems;

    /**
     * 比赛排名
     */
    private Integer contestRank;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
} 