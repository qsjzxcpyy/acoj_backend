package com.qsj.acoj.judge;

import com.qsj.acoj.model.entity.QuestionSubmit;

public interface JudgeService {
     QuestionSubmit doJudge(long questionSubmitId);
}
