package com.qsj.acoj.model.dto.admin;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class HandleApplicationRequest implements Serializable {
    /**
     * 申请ID
     */
    @NotNull(message = "申请ID不能为空")
    private Long applicationId;

    /**
     * 处理结果（1-通过，2-拒绝）
     */
    @NotNull(message = "处理结果不能为空")
    private Integer status;

    /**
     * 处理结果说明
     */
    private String result;

    private static final long serialVersionUID = 1L;
} 