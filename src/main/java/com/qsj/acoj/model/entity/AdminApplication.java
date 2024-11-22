package com.qsj.acoj.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("admin_application")
public class AdminApplication implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 申请用户id
     */
    private Long userId;

    /**
     * 申请理由
     */
    private String reason;

    /**
     * 申请状态（0-待处理，1-通过，2-拒绝）
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
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    private static final long serialVersionUID = 1L;
} 