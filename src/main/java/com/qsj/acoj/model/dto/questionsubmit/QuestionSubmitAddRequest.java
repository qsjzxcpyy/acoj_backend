package com.qsj.acoj.model.dto.questionsubmit;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 创建请求
 *
 *
 */
@Data
public class QuestionSubmitAddRequest implements Serializable {
    /**
     * 题目 id
     */
    private Long questionId;


    /**
     * 编程语言
     */
    private String language;

    /**
     * 提交的代码
     */
    private String code;

    private static final long serialVersionUID = 1L;
}