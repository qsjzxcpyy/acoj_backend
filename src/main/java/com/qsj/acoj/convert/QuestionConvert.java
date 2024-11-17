package com.qsj.acoj.convert;

import com.qsj.acoj.model.entity.AiQuestionChat;
import com.qsj.acoj.model.vo.AiChatRecordVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @Description
 * @Author qsj
 * @Date 2024/09/03
 */
@Mapper
public interface QuestionConvert {
    QuestionConvert INSTANCE = Mappers.getMapper(QuestionConvert.class);

    /**
     * 将AiQuestionChat列表转换为AiChatRecordVo列表
     */
    List<AiChatRecordVo> mapTo(List<AiQuestionChat> aiQuestionChats);

    /**
     * 将单个AiQuestionChat转换为AiChatRecordVo
     */
    AiChatRecordVo mapTo(AiQuestionChat aiQuestionChat);
}
