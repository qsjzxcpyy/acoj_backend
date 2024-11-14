package com.qsj.acoj.model.dto.contest;

/**
 * @Description
 * @Author qsj
 * @Date 2024/11/13
 */

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class ContestUpdateRequest implements Serializable {
    /**
     * 比赛id
     */
    @NotNull(message = "比赛id不能为空")
    private Long id;

    /**
     * 比赛名称
     */
    @NotBlank(message = "比赛名称不能为空")
    private String title;

    /**
     * 比赛描述
     */
    private String description;

    /**
     * 比赛开始时间
     */
    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 比赛结束时间
     */
    @NotNull(message = "结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 题目列表
     */
    private List<ContestProblemRequest> questions;

    private static final long serialVersionUID = 1L;
}

