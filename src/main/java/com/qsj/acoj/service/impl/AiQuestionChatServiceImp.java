package com.qsj.acoj.service.impl;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.qsj.acoj.common.ErrorCode;
import com.qsj.acoj.constant.AiConstant;
import com.qsj.acoj.exception.BusinessException;
import com.qsj.acoj.mapper.AiAccessTokenMapper;
import com.qsj.acoj.mapper.AiQuestionChatMapper;
import com.qsj.acoj.model.entity.AiAccessToken;
import com.qsj.acoj.model.entity.AiQuestionChat;
import com.qsj.acoj.model.vo.LoginUserVO;
import com.qsj.acoj.service.AiQuestionChatService;
import com.qsj.acoj.service.UserService;
import com.qsj.acoj.utils.RedisTokenUtils;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author qsj
 * @Date 2024/09/01
 */
@Service
public class AiQuestionChatServiceImp extends ServiceImpl<AiQuestionChatMapper, AiQuestionChat> implements AiQuestionChatService {

    @Resource
    private RedisTokenUtils redisTokenUtils;
    @Resource
    private AiAccessTokenMapper aiAccessTokenMapper;

    @Resource
    private UserService userService;


    OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder()
            .connectTimeout(3, TimeUnit.MINUTES)
            .writeTimeout(3, TimeUnit.MINUTES)
            .readTimeout(3, TimeUnit.MINUTES)
            .build();

    @Override
    public String getChatResponse(String question, HttpServletRequest request) throws IOException {
        LoginUserVO loginUser = userService.getLoginUser(request);

        String accessToken = redisTokenUtils.getAiAccessToken();
        if (accessToken == null) {
            AiAccessToken aiAccessToken = aiAccessTokenMapper.selectById(1L);
            accessToken = aiAccessToken.getAccessToken();
        }
        if (accessToken == null) {
            throw new BusinessException(ErrorCode.NO_AI_ACCESS_TOKEN);
        }
        String conent = createJsonContent(question);


        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, conent);
        Request request1 = new Request.Builder()
                .url("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/yi_34b_chat?access_token=" + accessToken)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request1).execute();

        String jsonResponse = response.body().string();
        JSONObject jsonObject = new JSONObject(jsonResponse);
        String ans = null;
        if (jsonObject.has("result")) {
            ans = jsonObject.getString("result");
        } else {
            throw new BusinessException(ErrorCode.AI_API_REQUEST_ERROR_CODE, jsonObject.getString("error_msg"));
        }


        // 保存聊天
        AiQuestionChat aiQuestionChat = new AiQuestionChat();
        aiQuestionChat.setUserId(loginUser.getId());
        aiQuestionChat.setUserName(loginUser.getUserName());
        aiQuestionChat.setUserRequest(question);
        aiQuestionChat.setAiResponse(ans);
        this.save(aiQuestionChat);

        return ans;
    }


    private static String createJsonContent(String userInput) throws IOException {
        // 使用 Jackson 的 ObjectMapper 创建 JSON 对象
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode messageNode = mapper.createObjectNode();
        messageNode.put("role", "user");
        messageNode.put("content", userInput);

        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.set("messages", mapper.createArrayNode().add(messageNode));

        // 将 JSON 对象转换为字符串
        return mapper.writeValueAsString(rootNode);
    }


    @Override
    public List<AiQuestionChat> listTopQuestionChat(HttpServletRequest request) {
        LoginUserVO loginUser = userService.getLoginUser(request);
        // 从数据库中获取用户最近的两天之内的聊天数据

        // 获取当前时间的两天前
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);


        // 创建 LambdaQueryWrapper
        LambdaQueryWrapper<AiQuestionChat> wrapper = Wrappers.<AiQuestionChat>lambdaQuery().eq(AiQuestionChat::getUserId, loginUser.getId())
                .ge(AiQuestionChat::getCreateTime, twoDaysAgo);

        return this.list(wrapper);


    }
}


