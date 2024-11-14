package com.qsj.acoj.model.vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class ContestProblemStatisticsVO implements Serializable {
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
     * 总提交数
     */
    private Integer totalSubmissions;
    
    /**
     * 通过数
     */
    private Integer acceptedCount;
    
    /**
     * 通过率
     */
    private Double acceptedRate;

    private static final long serialVersionUID = 1L;
} 