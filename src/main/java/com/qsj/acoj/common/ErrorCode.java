package com.qsj.acoj.common;

/**
 * 自定义错误码
 *
 *
 */
public enum ErrorCode {

    SUCCESS(0, "ok"),
    PARAMS_ERROR(40000, "请求参数错误"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    NO_AUTH_ERROR(40101, "无权限"),
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    FORBIDDEN_ERROR(40300, "禁止访问"),
    SYSTEM_ERROR(50000, "系统内部异常"),
    OPERATION_ERROR(50001, "操作失败"),

    API_REQUEST_ERROR(50010,"接口调用失败"),
    REFRESH_TOKEN_EXPIRED(50020,"刷新令牌过期"),
    ACCESS_TOKEN_EXPIRED(50030,"访问令牌过期"),
    NO_AI_ACCESS_TOKEN(50040,"无访问令牌"),
    // 请求失败
    AI_API_REQUEST_ERROR_CODE(50050,"请求失败");


    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
