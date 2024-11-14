package com.qsj.acoj.model.dto.contest;

import lombok.Data;
import java.io.Serializable;

@Data
public class  ContestQueryRequest implements Serializable {
    /**
     * 比赛ID
     */
    private Long id;

    /**
     * 比赛名称
     */
    private String title;

    /**
     * 比赛状态
     */
    private String status;

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