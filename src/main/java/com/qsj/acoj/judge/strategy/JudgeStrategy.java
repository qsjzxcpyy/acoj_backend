package com.qsj.acoj.judge.strategy;

import com.qsj.acoj.judge.codesandbox.model.JudgeInfo;

public interface JudgeStrategy {
    public JudgeInfo doJudge(JudgeContext judgeContext);
}
