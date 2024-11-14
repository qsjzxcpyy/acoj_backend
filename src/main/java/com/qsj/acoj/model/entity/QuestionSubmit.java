package com.qsj.acoj.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 判题任务表
 * @TableName question_submit
 */
@TableName(value ="question_submit")
@Data
public class QuestionSubmit implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 提交的代码
     */
    private String code;

    /**
     * 题目测评状态，0 未测评 1 正在测评 2 测评成功 3 测评失败
     */
    private Integer status;

    /**
     * accept wonger answer TLE ....,消耗的时间和占用的空间 （json 数组）
     */
    private String judgeInfo;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}