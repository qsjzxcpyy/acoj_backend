package com.qsj.acoj.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.log.Log;
import com.qsj.acoj.constant.TokenConstant;
import com.qsj.acoj.mapper.AccessTokenMapper;
import com.qsj.acoj.mapper.RefreshTokenMapper;
import com.qsj.acoj.model.entity.AccessToken;
import com.qsj.acoj.model.entity.RefreshToken;
import com.qsj.acoj.model.entity.User;
import com.qsj.acoj.model.vo.LoginUserVO;
import com.qsj.acoj.service.UserService;
import com.qsj.acoj.service.UserTokenService;
import com.qsj.acoj.utils.RedisTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

@Service
@Validated
@Slf4j
public class UserTokenServiceImpl implements UserTokenService {
    @Resource
    @Lazy // 懒加载，避免循环依赖
    private UserService userService;
    @Resource
    private AccessTokenMapper accessTokenMapper;
    @Resource
    private RefreshTokenMapper refreshTokenMapper;

    @Resource
    private RedisTokenUtils redisTokenUtils;

    @Override
    public AccessToken getUserAccessToken(RefreshToken refreshToken) {
        AccessToken accessToken = new AccessToken()
                .setUserId(refreshToken.getUserId()).setUserInfo(BuildUserInfo(refreshToken.getUserId()))
                .setAccessToken(generateAccessToken())
                .setExpiresTime(LocalDateTime.now().plusSeconds(TokenConstant.ACCESS_TOKEN_EXPIRES_TIME))
                .setRefreshToken(refreshToken.getRefreshToken());
        accessTokenMapper.insert(accessToken);
        //缓存到 redis 中
        redisTokenUtils.set(accessToken);

        return accessToken;
    }

    @Override
    public RefreshToken getUserRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken()
                .setRefreshToken(generateRefreshToken())
                .setUserId(userId)
                .setExpiresTime(LocalDateTime.now().plusSeconds(TokenConstant.REFRESH_TOKEN_EXPIRES_TIME));
        refreshTokenMapper.insert(refreshToken);
        return refreshToken;
    }

    public static String generateRefreshToken() {
        return IdUtil.fastSimpleUUID();
    }

    public static String generateAccessToken() {
        return IdUtil.fastSimpleUUID();
    }

    private Map<String, String> BuildUserInfo(Long userId) {
        User user = userService.getById(userId);
        return MapUtil.builder(LoginUserVO.INFO_KEY_USERNAME, user.getUserName())
                .put(LoginUserVO.INFO_KEY_USERAVATAR, user.getUserAvatar())
                .put(LoginUserVO.INFO_KEY_USERPROFILE, user.getUserProfile())
                .put(LoginUserVO.INFO_KEY_USERROLE, user.getUserRole())
                .build();
    }


    @Override
    public AccessToken removeToken(String accessToken) {
        AccessToken accessToken1 = accessTokenMapper.selectByAccessToke(accessToken);
        if (accessToken1 == null) return null;
        accessTokenMapper.deleteById(accessToken1.getId());
        redisTokenUtils.del(accessToken1.getAccessToken());

        refreshTokenMapper.deleteByToken(accessToken1.getRefreshToken());
        return accessToken1;

    }



}


