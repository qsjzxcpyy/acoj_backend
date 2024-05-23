package com.qsj.acoj.judge;

import com.qsj.acoj.judge.strategy.DefaultJudgeStrategyImpl;
import com.qsj.acoj.judge.strategy.JavaLanguageJudgeStrategyImpl;
import com.qsj.acoj.judge.strategy.JudgeContext;
import com.qsj.acoj.judge.strategy.JudgeStrategy;
import com.qsj.acoj.judge.codesandbox.model.JudgeInfo;
import org.springframework.stereotype.Service;

@Service
public class JudgeManage{
    public JudgeInfo doJudge(JudgeContext judgeContext){
         String language = judgeContext.getQuestionSubmit().getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategyImpl();
        if(language.equals("java")){
            judgeStrategy = new JavaLanguageJudgeStrategyImpl();
        }
        return judgeStrategy.doJudge(judgeContext);

    }
}
