package com.qsj.acoj.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.qsj.acoj.model.dto.question.JudgeCase;
import com.qsj.acoj.model.dto.question.JudgeConfig;
import com.qsj.acoj.judge.codesandbox.model.JudgeInfo;
import com.qsj.acoj.model.entity.Question;
import com.qsj.acoj.model.entity.QuestionSubmit;
import com.qsj.acoj.model.enums.JudgeInfoMessageEnum;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JavaLanguageJudgeStrategyImpl implements JudgeStrategy {
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        JudgeInfo finalJudgeInfo = new JudgeInfo();
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        List<String> outputList = judgeContext.getOutputList();
        Question question = judgeContext.getQuestion();
        List<JudgeCase> judgeCase = judgeContext.getJudgeCase();
        List<String> limitOutputList = judgeCase.stream().map(JudgeCase::getOutput).collect(Collectors.toList());
        Long memory = Optional.ofNullable(judgeInfo.getMemory()).orElse(0L);
        finalJudgeInfo.setMemory(memory);
        Long time = Optional.ofNullable(judgeInfo.getTime()).orElse(0L);
        finalJudgeInfo.setTime(time);
        Integer status = judgeContext.getStatus();
        //根据输出用例进行判断代码执行信息

        if(status.equals(2)){
            finalJudgeInfo.setMessage(JudgeInfoMessageEnum.COMPILE_ERROR.getText());
            return finalJudgeInfo;
        }
        if(status.equals(3)){
            finalJudgeInfo.setMessage(JudgeInfoMessageEnum.RUNNING_ERROR.getText());
            return finalJudgeInfo;
        }
        if (outputList.size() != limitOutputList.size()) {
            finalJudgeInfo.setMessage(JudgeInfoMessageEnum.WRONG_ANSWER.getText());
            return finalJudgeInfo;
        }

        for (int i = 0; i < outputList.size(); i++) {

            String limtOutput = limitOutputList.get(i);
            if (!limtOutput.equals(outputList.get(i).trim())) {
                finalJudgeInfo.setMessage(JudgeInfoMessageEnum.WRONG_ANSWER.getText());
                return finalJudgeInfo;
            }

        }

        //根据题目限制判断代码执行信息
        String limitJudgeConfigStr = question.getJudgeConfig();
        JudgeConfig limitJudgeConfig = JSONUtil.toBean(limitJudgeConfigStr, JudgeConfig.class);


        if(limitJudgeConfig.getMemoryLimit() * 1024 < memory){
            finalJudgeInfo.setMessage(JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED.getText());
            return finalJudgeInfo;
        }
        long JAVA_PROGRAM_TIME_COST = 800l;
        if(limitJudgeConfig.getTimeLimit() * 1000 < judgeInfo.getTime() - JAVA_PROGRAM_TIME_COST){
            finalJudgeInfo.setMessage(JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED.getText());
            return finalJudgeInfo;
        }

        finalJudgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getText());
        return finalJudgeInfo;


    }
}
