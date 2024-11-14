package com.qsj.acoj.model.vo;

import com.qsj.acoj.model.entity.Question;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ContestVO implements Serializable {
    /**
     * 比赛ID
     */
    private Long id;

    /**
     * 比赛名称
     */
    private String name;

    /**
     * 比赛描述
     */
    private String description;

    /**
     * 比赛开始时间
     */
    private LocalDateTime startTime;

    /**
     * 比赛结束时间
     */
    private LocalDateTime endTime;

    /**
     * 比赛状态
     */
    private String status;

    /**
     * 参与人数
     */
    private Integer participantCount;

    /**
     * 题目列表
     */
    private List<ContestQuestionVO> problems;

    /**
     * 当前用户是否已报名
     */
    private Boolean isRegistered;

    /**
     * 创建者id
     */
    private Long userId;

    /**
     * 创建者信息
     */
    private UserVO userVO;

    private static final long serialVersionUID = 1L;
} 