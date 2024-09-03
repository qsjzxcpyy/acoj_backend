package com.qsj.acoj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qsj.acoj.model.entity.AiQuestionChat;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * @Description
 * @Author qsj
 * @Date 2024/09/01
 */
public interface AiQuestionChatService extends IService<AiQuestionChat> {

    String getChatResponse(String question, HttpServletRequest request) throws IOException;

    List<AiQuestionChat> listTopQuestionChat(HttpServletRequest request);
}
