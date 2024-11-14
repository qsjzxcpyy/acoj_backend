package com.qsj.acoj.model.vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class ContestProblemDetailVO implements Serializable {
    /**
     * 题目ID
     */
    private Long problemId;
    
    /**
     * 题目序号
     */
    private Integer problemOrder;
    
    /**
     * 题目名称
     */
    private String problemTitle;
    
    /**
     * 是否通过
     */
    private Boolean accepted;
    
    /**
     * 提交次数
     */
    private Integer submitCount;
    
    /**
     * 第一次通过时间（从比赛开始计时，单位：分钟）
     */
    private Integer firstAcceptedTime;
    
    /**
     * 罚时（分钟）
     */
    private Integer penaltyTime;

    private static final long serialVersionUID = 1L;
} 