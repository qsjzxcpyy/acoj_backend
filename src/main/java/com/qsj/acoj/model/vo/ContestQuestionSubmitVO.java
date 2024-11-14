package com.qsj.acoj.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ContestQuestionSubmitVO implements Serializable {
    /**
     * 题目ID
     */
    private Long questionId;
    
    /**
     * 题目序号
     */
    private Integer problemOrder;
    
    /**
     * 是否通过
     */
    private Boolean accepted;
    
    /**
     * 提交次数
     */
    private Integer submitCount;
    
    /**
     * 该题目的所有提交记录
     */
    private List<QuestionSubmitVO> submissions;

    private static final long serialVersionUID = 1L;
} 