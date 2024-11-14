package com.qsj.acoj.model.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class ContestRankDetailVO implements Serializable {
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户信息
     */
    private UserVO userVO;
    
    /**
     * 总分
     */
    private Integer totalScore;
    
    /**
     * 总用时（分钟）
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
     * 排名
     */
    private Integer rank;
    
    /**
     * 每道题的提交情况
     */
    private List<ContestProblemDetailVO> problemDetails;

    private static final long serialVersionUID = 1L;
} 