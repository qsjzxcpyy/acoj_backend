package com.qsj.acoj.constant;

import lombok.extern.slf4j.Slf4j;

public interface TokenConstant {
     long REFRESH_TOKEN_EXPIRES_TIME = 604800;
     long ACCESS_TOKEN_EXPIRES_TIME = 7200;

     String USER_ACCESS_TOKEN = "user_access_token:%s";

     String HEADER_ACCESS_TOKEN = "token:access";

}
