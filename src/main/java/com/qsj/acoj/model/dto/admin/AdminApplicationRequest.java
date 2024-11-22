package com.qsj.acoj.model.dto.admin;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class AdminApplicationRequest implements Serializable {
    /**
     * 申请理由
     */
    @NotBlank(message = "申请理由不能为空")
    private String reason;
    
    private static final long serialVersionUID = 1L;
} 