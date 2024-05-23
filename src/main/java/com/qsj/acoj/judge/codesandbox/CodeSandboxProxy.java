package com.qsj.acoj.judge.codesandbox;

import com.qsj.acoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.qsj.acoj.judge.codesandbox.model.ExecuteCodeResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 静态代理，在原先的代码前后添加代码，保证ocp原则，减少代码冗余。
 */
@Slf4j
@AllArgsConstructor
public class CodeSandboxProxy implements CodeSandbox{

    private final CodeSandbox codeSandbox;
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
       log.info("代码沙箱请求信息" + executeCodeRequest.toString());
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        log.info("代码沙箱返回信息" + executeCodeResponse.toString());
        return executeCodeResponse;
    }
}
