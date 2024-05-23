package com.qsj.acoj.model.dto.question;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 编辑请求
 *
 *
 */
@Data
public class QuestionEditRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表，题目类型（json 数组）
     */
    private List<String> tags;


    /**
     * 时间复杂度，空间复杂度
     */
    private JudgeConfig judgeConfig;

    /**
     * 输入样例输出样例
     */
    private List<JudgeCase> judgeCase;

    /**
     * 题目答案
     */
    private String answer;

    private static final long serialVersionUID = 1L;
}