package com.qsj.acoj.model.dto.questionsubmit;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.qsj.acoj.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 创建请求
 *
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionSubmitQueryRequest  extends PageRequest implements Serializable {


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
     * 题目测评状态，0 未测评 1 正在测评 2 测评成功 3 测评失败
     */
    private Integer status;



    private static final long serialVersionUID = 1L;
}