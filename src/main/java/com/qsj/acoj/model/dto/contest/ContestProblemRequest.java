package com.qsj.acoj.model.dto.contest;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description
 * @Author qsj
 * @Date 2024/11/13
 */
@Data
public class ContestProblemRequest implements Serializable {
    /**
     * 题目ID
     */
    private String id;

    /**
     * 题目顺序
     */
    private Integer order;

    private static final long serialVersionUID = 1L;
}