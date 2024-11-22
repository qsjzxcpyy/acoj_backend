package com.qsj.acoj.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AdminApplicationVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 申请用户id
     */
    private Long userId;

    /**
     * 申请用户信息
     */
    private UserVO userVO;

    /**
     * 申请理由
     */
    private String reason;

    /**
     * 申请状态
     */
    private Integer status;

    /**
     * 处理结果说明
     */
    private String result;

    /**
     * 处理人id
     */
    private Long handleUserId;

    /**
     * 处理人信息
     */
    private UserVO handleUserVO;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    private static final long serialVersionUID = 1L;
} 