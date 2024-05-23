package com.qsj.acoj.judge.codesandbox.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.qsj.acoj.common.ErrorCode;
import com.qsj.acoj.exception.BusinessException;
import com.qsj.acoj.judge.codesandbox.CodeSandbox;
import com.qsj.acoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.qsj.acoj.judge.codesandbox.model.ExecuteCodeResponse;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;


/**
 *使用自己完成的远程的代码沙箱
 */
public class RemoteCodeSandboxImpl implements CodeSandbox {

    private static final String AUTH_REQUEST_HEADER = "auth";

    private static final String AUTH_REQUEST_SECRET = "secretKey";
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
       String url = "http://localhost:8089/executeCode";
        String executeCodeRequestStr = JSONUtil.toJsonStr(executeCodeRequest);
        String executeCodeResponseStr = HttpUtil.createPost(url)
                .header(AUTH_REQUEST_HEADER,AUTH_REQUEST_SECRET)
                .body(executeCodeRequestStr)
                .execute()
                .body();
        if(StringUtils.isBlank(executeCodeResponseStr)){
             throw new BusinessException(ErrorCode.API_REQUEST_ERROR,"execute remoteSandbox error, message =" + executeCodeRequestStr);
        }
        return JSONUtil.toBean(executeCodeResponseStr, ExecuteCodeResponse.class);



    }
}
