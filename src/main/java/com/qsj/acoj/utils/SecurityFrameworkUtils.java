package com.qsj.acoj.utils;

import javax.servlet.http.HttpServletRequest;

public class SecurityFrameworkUtils {
    private SecurityFrameworkUtils() {};

    public static String getAccessTokenFromRequest(HttpServletRequest request,String headerName) {
        String accessToken = request.getHeader(headerName);
        return accessToken;
    }

}
