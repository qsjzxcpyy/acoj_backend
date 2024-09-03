package com.qsj.acoj.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qsj.acoj.constant.AiConstant;
import com.qsj.acoj.mapper.AiAccessTokenMapper;
import com.qsj.acoj.mapper.AiQuestionChatMapper;
import com.qsj.acoj.model.entity.AiAccessToken;
import com.qsj.acoj.model.entity.AiQuestionChat;
import com.qsj.acoj.utils.JedisUtils;
import okhttp3.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import org.json.JSONObject;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;
import java.time.LocalDateTime;


/**
 * @Description
 * @Author qsj
 * @Date 2024/09/01
 */
@Component
public class RefreshAiAccessToken {

    @Resource
    AiQuestionChatMapper aiQuestionChatMapper;

    @Resource
    AiAccessTokenMapper aiAccessTokenMapper;

    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();


    @PostConstruct
    public void init() {
        // 应用启动时调用一次 refreshAccessToken 方法
        refreshAccessToken();
    }

    @Scheduled(cron = "0 0 0 */29 * ?") // 每29天执行一次，确保在token过期之前刷新
    public void refreshAccessToken() {
        try {
            String token = getAccessToken();
            updateAccessTokenInRedis(token);

            AiAccessToken aiAccessToken = new AiAccessToken();
            aiAccessToken.setId(1L); // 假设 id 为 1 是唯一标识
            aiAccessToken.setAccessToken(token);
            aiAccessToken.setExpiresTime(calculateExpirationTime()); // 根据实际需求计算过期时间
            aiAccessToken.setCreateTime(LocalDateTime.now()); // 设置创建时间
            if(aiAccessTokenMapper.selectById(1L) != null) {
                aiAccessTokenMapper.deleteById(1L);
            }
            aiAccessTokenMapper.insert(aiAccessToken);
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 0 5 * * ?") // 每天凌晨5点执行一次
    public void deleteOldChatData() {
        // 获取两天前的时间
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);

        // 删除两天之前的数据
        aiQuestionChatMapper.delete(new LambdaQueryWrapper<AiQuestionChat>()
                .lt(AiQuestionChat::getCreateTime, twoDaysAgo));
    }


    private LocalDateTime calculateExpirationTime() {
        // 计算 token 的过期时间，通常是当前时间加上有效期
        // 示例：假设 token 有效期为 30 天
        return LocalDateTime.now().plusDays(30);
    }

    private void updateAccessTokenInRedis(String token) {
        Jedis jedis = JedisUtils.getJedis();
            jedis.set(AiConstant.AI_ACCESS_TOKEN, token);
            JedisUtils.close(jedis);
    }

    private String getAccessToken() throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + AiConstant.API_KEY
                + "&client_secret=" + AiConstant.SECRET_KEY);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        return new JSONObject(response.body().string()).getString("access_token");
    }
}
