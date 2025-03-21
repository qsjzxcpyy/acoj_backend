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
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

//        String accessToken = redisTokenUtils.getAiAccessToken();
//        if (accessToken == null) {
            AiAccessToken aiAccessToken = aiAccessTokenMapper.selectById(1L);
            String apiKey = aiAccessToken.getAccessToken();
//        }
        if (apiKey == null) {
            throw new BusinessException(ErrorCode.NO_AI_ACCESS_TOKEN);
        }
//        String conent = createJsonContent(question);


//        MediaType mediaType = MediaType.parse("application/json");
//        RequestBody body = RequestBody.create(mediaType, conent);
//        Request request1 = new Request.Builder()
//                .url("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/yi_34b_chat?access_token=" + accessToken)
//                .method("POST", body)
//                .addHeader("Content-Type", "application/json")
//                .build();
//        Response response = HTTP_CLIENT.newCall(request1).execute();
//
//        String jsonResponse = response.body().string();
//        JSONObject jsonObject = new JSONObject(jsonResponse);
        ArrayList<String> ans = new ArrayList<>();
//        if (jsonObject.has("result")) {
//            ans = jsonObject.getString("result");
//        } else {
//            throw new BusinessException(ErrorCode.AI_API_REQUEST_ERROR_CODE, jsonObject.getString("error_msg"));
//        }



        // 创建ArkService实例
        ArkService arkService = ArkService.builder().apiKey(apiKey).build();

        // 初始化消息列表
        List<ChatMessage> chatMessages = new ArrayList<>();

        // 创建用户消息
        ChatMessage userMessage = ChatMessage.builder()
                .role(ChatMessageRole.USER) // 设置消息角色为用户
                .content(question) // 设置消息内容
                .build();

        // 将用户消息添加到消息列表
        chatMessages.add(userMessage);

        // 创建聊天完成请求
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("deepseek-v3-241226")// 需要替换为Model ID
                .messages(chatMessages) // 设置消息列表
                .build();

        // 发送聊天完成请求并打印响应
        try {
            // 获取响应并打印每个选择的消息内容
            arkService.createChatCompletion(chatCompletionRequest)
                    .getChoices()
                    .forEach(choice -> ans.add((String)choice.getMessage().getContent()));
        } catch (Exception e) {
            System.out.println("请求失败: " + e.getMessage());
        } finally {
            // 关闭服务执行器
            arkService.shutdownExecutor();
        }



        // 保存聊天
        AiQuestionChat aiQuestionChat = new AiQuestionChat();
        aiQuestionChat.setUserId(loginUser.getId());
        aiQuestionChat.setUserName(loginUser.getUserName());
        aiQuestionChat.setUserRequest(question);
        aiQuestionChat.setAiResponse(ans.get(0));
        this.save(aiQuestionChat);

        return ans.get(0);
    }


//    private static String createJsonContent(String userInput) throws IOException {
//        // 使用 Jackson 的 ObjectMapper 创建 JSON 对象
//        ObjectMapper mapper = new ObjectMapper();
//        ObjectNode messageNode = mapper.createObjectNode();
//        messageNode.put("role", "user");
//        messageNode.put("content", userInput);
//
//        ObjectNode rootNode = mapper.createObjectNode();
//        rootNode.set("messages", mapper.createArrayNode().add(messageNode));
//
//        // 将 JSON 对象转换为字符串
//        return mapper.writeValueAsString(rootNode);
//    }


    @Override
    public List<AiQuestionChat> listTopQuestionChat(HttpServletRequest request) {
        LoginUserVO loginUser = userService.getLoginUser(request);
        // 从数据库中获取用户最近的两天之内的聊天数据

        // 获取当前时间的两天前
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);


        // 创建 LambdaQueryWrapper
        LambdaQueryWrapper<AiQuestionChat> wrapper = Wrappers.<AiQuestionChat>lambdaQuery().eq(AiQuestionChat::getUserId, loginUser.getId())
                .ge(AiQuestionChat::getCreateTime, twoDaysAgo);

        List<AiQuestionChat> aiQuestionChats = this.list(wrapper);
            System.out.println("aiQuestionChats = " + aiQuestionChats);
        return aiQuestionChats;


    }
}


    