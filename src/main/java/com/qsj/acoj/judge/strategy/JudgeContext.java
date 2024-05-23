package com.qsj.acoj.judge.strategy;

import com.qsj.acoj.model.dto.question.JudgeCase;
import com.qsj.acoj.judge.codesandbox.model.JudgeInfo;
import com.qsj.acoj.model.entity.Question;
import com.qsj.acoj.model.entity.QuestionSubmit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JudgeContext {
    private JudgeInfo judgeInfo;
    private List<String> inputList;
    private List<String> outputList;
    private Question question;
    private QuestionSubmit questionSubmit;
    private List<JudgeCase> judgeCase;
    private Integer status;


}
