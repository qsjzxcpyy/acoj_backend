package com.qsj.acoj.model.entity;

/**
 * @Description
 * @Author qsj
 * @Date 2024/09/01
 */
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName("ai_question_chat")
@Data
@EqualsAndHashCode
public class AiQuestionChat implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

    private Long userId;

    private String userName;

    private String userRequest;

    private String aiResponse;

    private LocalDateTime createTime;
}
