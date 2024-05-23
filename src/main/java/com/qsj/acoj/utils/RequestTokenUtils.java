package com.qsj.acoj.utils;

import com.qsj.acoj.common.ResultUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.UUID;

public class RequestTokenUtils {
    public static String getRequestToken(){
        Jedis jedis = JedisUtils.getJedis();
        String token = UUID.randomUUID().toString();
        jedis.setex(token, 60 * 60, "1");
        JedisUtils.close(jedis);
        return token;
    }

    public static Boolean verify (String token){
        Jedis jedis = JedisUtils.getJedis();
        Transaction multi = jedis.multi();
        multi.get(token);
        multi.del(token);
        Boolean res = multi.exec().get(0) != null;
        JedisUtils.close(jedis);
        return res;
    }
}
