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
    // 单个对象映射方法
    AiChatRecordVo map(AiQuestionChat aiQuestionChat)   ;

    // AiQuestionChat 转换 AiQuestionChatVO
    public List<AiChatRecordVo> mapTo(List<AiQuestionChat> aiQuestionChat);

}
