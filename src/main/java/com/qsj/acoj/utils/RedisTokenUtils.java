package com.qsj.acoj.utils;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.json.JSONUtil;
import com.qsj.acoj.constant.TokenConstant;
import com.qsj.acoj.model.entity.AccessToken;
import io.github.classgraph.json.JSONUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisTokenUtils {
    @Resource
    RedisTemplate redisTemplate;

    public AccessToken get(String accessToken) {
        String redisKey = formatKey(accessToken);
        return JsonUtils.parseObject((String)redisTemplate.opsForValue().get(redisKey),AccessToken.class);
    }
    public void set(AccessToken accessToken) {
        String redisKey = formatKey(accessToken.getAccessToken());
        accessToken.setCreateTime(null).setIsDelete(null).setUpdateTime(null);
        long time = LocalDateTimeUtil.between(LocalDateTime.now(), accessToken.getExpiresTime(), ChronoUnit.SECONDS);
        if(time > 0) {
            redisTemplate.opsForValue().set(redisKey, JsonUtils.toJsonString(accessToken), time, TimeUnit.SECONDS);
        }

    }

    public void del(String access) {
        String redisKey = formatKey(access);
        redisTemplate.delete(redisKey);
    }
    private static String  formatKey(String accessToken) {
        return String.format(TokenConstant.USER_ACCESS_TOKEN,accessToken);
    }
    public void deleteList(Collection<String> accessTokens) {
        List<String> redisKeys = CollectionUtils.convertList(accessTokens, RedisTokenUtils::formatKey);
        redisTemplate.delete(redisKeys);
    }

}
