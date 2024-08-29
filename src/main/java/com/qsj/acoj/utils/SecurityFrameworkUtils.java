package com.qsj.acoj.utils;

import com.qsj.acoj.model.vo.LoginUserVO;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

public class SecurityFrameworkUtils {
    private SecurityFrameworkUtils() {};
    @Nullable
    public static LoginUserVO getLoginUserVO() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return null;
        }
        return authentication.getPrincipal() instanceof LoginUserVO ? (LoginUserVO) authentication.getPrincipal() : null;
    }

    public static Authentication getAuthentication() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) {
            return null;
        }
        return context.getAuthentication();
    }
    @Nullable
    public static Long getLoginUserId() {
        LoginUserVO loginUser = getLoginUserVO();
        return loginUser != null ? loginUser.getId() : null;
    }


    public static String getAccessTokenFromRequest(HttpServletRequest request,String headerName) {
        String accessToken = request.getHeader(headerName);
        return accessToken;
    }

    public static void setLoginUserVO(LoginUserVO loginUserVO, HttpServletRequest request) {
        Authentication authentication = buildAuthentication(loginUserVO, request);
        SecurityContextHolder.getContext().setAuthentication(authentication);

    }
    public static Authentication buildAuthentication(LoginUserVO loginUserVO, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginUserVO, null, Collections.emptyList());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return authenticationToken;
    }


}
