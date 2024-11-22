package com.qsj.acoj.model.dto.question;

import lombok.Data;
import java.io.Serializable;

@Data
public class QuestionSubmitAddRequest implements Serializable {
    /**
     * 编程语言
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 比赛 id
     */
    private Long contestId;

    private static final long serialVersionUID = 1L;
} 