package com.qsj.acoj.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.qsj.acoj.model.dto.question.JudgeCase;
import com.qsj.acoj.model.dto.question.JudgeConfig;
import com.qsj.acoj.judge.codesandbox.model.JudgeInfo;
import com.qsj.acoj.model.entity.Question;
import com.qsj.acoj.model.entity.QuestionSubmit;
import com.qsj.acoj.model.enums.JudgeInfoMessageEnum;

import java.util.List;
import java.util.stream.Collectors;

public class DefaultJudgeStrategyImpl implements JudgeStrategy {
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        JudgeInfo finalJudgeInfo = new JudgeInfo();
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        Question question = judgeContext.getQuestion();
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        List<JudgeCase> judgeCase = judgeContext.getJudgeCase();
        List<String> limitOutputList = judgeCase.stream().map(JudgeCase::getOutput).collect(Collectors.toList());
        finalJudgeInfo.setMemory(judgeInfo.getMemory());
        finalJudgeInfo.setTime(judgeInfo.getTime());
        //根据输出用例进行判断代码执行信息

        if (outputList.size() != limitOutputList.size()) {
            finalJudgeInfo.setMessage(JudgeInfoMessageEnum.WRONG_ANSWER.getText());
            return finalJudgeInfo;
        }

        for (int i = 0; i < outputList.size(); i++) {
            String limtOutput = limitOutputList.get(i);
            if (!limtOutput.equals(outputList.get(i))) {
               finalJudgeInfo.setMessage(JudgeInfoMessageEnum.WRONG_ANSWER.getText());
               return finalJudgeInfo;
            }

        }

        //根据题目限制判断代码执行信息
        String limitJudgeConfigStr = question.getJudgeConfig();
        JudgeConfig limitJudgeConfig = JSONUtil.toBean(limitJudgeConfigStr, JudgeConfig.class);
        if(limitJudgeConfig.getMemoryLimit() < judgeInfo.getMemory()){
            finalJudgeInfo.setMessage(JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED.getText());
            return finalJudgeInfo;
        }

        if(limitJudgeConfig.getTimeLimit() < judgeInfo.getTime()){
            finalJudgeInfo.setMessage(JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED.getText());
            return finalJudgeInfo;
        }

        finalJudgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getText());
        return finalJudgeInfo;


    }
}
