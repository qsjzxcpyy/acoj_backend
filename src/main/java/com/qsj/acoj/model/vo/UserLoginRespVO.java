package com.qsj.acoj.model.vo;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserLoginRespVO {
    /**
     * 用户Id
     */

    private Long userId;

    /**
     * 用户信息
     */
    private Map<String,String> userInfo;
    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 访问令牌过期时间
     */
    private LocalDateTime expiresTime;



}
