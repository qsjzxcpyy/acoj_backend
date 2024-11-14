package com.qsj.acoj.model.vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class ContestQuestionVO extends QuestionVO implements Serializable {
    /**
     * 题目在比赛中的序号
     */
    private Integer problemOrder;

    private static final long serialVersionUID = 1L;
} 