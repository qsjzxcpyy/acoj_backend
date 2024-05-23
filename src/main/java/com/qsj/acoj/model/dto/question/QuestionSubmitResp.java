package com.qsj.acoj.model.dto.question;

import lombok.Data;

@Data
public class QuestionSubmitResp {
    private Long questionSubmitId;
    //题目测评完后，刷新RequestToken
    private String refreshToken;
}
