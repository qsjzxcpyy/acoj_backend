package com.qsj.acoj.model.dto.admin;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminApplicationQueryRequest implements Serializable {
    /**
     * 申请状态（0-待处理，1-通过，2-拒绝）
     */
    private Integer status;
    
    /**
     * 当前页号
     */
    private long current = 1;

    /**
     * 页面大小
     */
    private long pageSize = 10;

    private static final long serialVersionUID = 1L;
} 