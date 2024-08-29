package com.qsj.acoj.service;

import com.qsj.acoj.model.entity.AccessToken;
import com.qsj.acoj.model.entity.RefreshToken;
import com.qsj.acoj.model.entity.User;

public interface UserTokenService {
    /**
     * 获得访问令牌
     * @param
     * @return
     */
  AccessToken getUserAccessToken(RefreshToken refreshToken);

    /**
     * 获得刷新令牌
     * @param userId
     * @return
     */
  RefreshToken getUserRefreshToken(Long userId);

  AccessToken removeToken(String accessToken);

  AccessToken refreshToken(String refreshToken);

  AccessToken checkAccessToken(String accessToken);


}
