package com.qsj.acoj.model.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;
import org.checkerframework.checker.units.qual.A;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Description
 * @Author qsj
 * @Date 2024/09/03
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiChatRecordVo implements Serializable {

    private static final long serialVersionUID = 1084404118672320856L;

    private Long userId;

    private String userName;

    private String userRequest;

    private String aiResponse;

    private LocalDateTime createTime;
}
