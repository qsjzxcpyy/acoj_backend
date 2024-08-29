package com.qsj.acoj.filter;

import cn.hutool.core.util.StrUtil;
import com.qsj.acoj.common.BaseResponse;
import com.qsj.acoj.common.ErrorCode;
import com.qsj.acoj.constant.TokenConstant;
import com.qsj.acoj.exception.BusinessException;
import com.qsj.acoj.exception.GlobalExceptionHandler;
import com.qsj.acoj.model.entity.AccessToken;
import com.qsj.acoj.model.entity.User;
import com.qsj.acoj.model.vo.LoginUserVO;
import com.qsj.acoj.service.UserService;
import com.qsj.acoj.service.UserTokenService;
import com.qsj.acoj.utils.BeanUtils;
import com.qsj.acoj.utils.SecurityFrameworkUtils;
import com.qsj.acoj.utils.ServletUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@RequiredArgsConstructor
@Component
public class UserTokenFilter extends OncePerRequestFilter {
    @Resource
    UserTokenService userTokenService;
    @Resource
    UserService userService;
    private  final GlobalExceptionHandler globalExceptionHandler;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String accessToken = SecurityFrameworkUtils.getAccessTokenFromRequest(request, TokenConstant.HEADER_ACCESS_TOKEN);
            if (StrUtil.isNotBlank(accessToken)) {
                LoginUserVO loginUserVO = buildLoginUserVO(accessToken);
                if(loginUserVO != null) {
                    SecurityFrameworkUtils.setLoginUserVO(loginUserVO,request);
                } else {
                    throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR,"Token 校验失败,请重新登录");
                }
            }
        } catch (BusinessException businessException) {
//            BaseResponse<?> baseResponse = globalExceptionHandler.businessExceptionHandler(businessException);
//            ServletUtils.writeJSON(response,baseResponse);
        }
        filterChain.doFilter(request,response);


    }

    private LoginUserVO buildLoginUserVO(String accessToken) {
        try {
            AccessToken accessToken1 = userTokenService.checkAccessToken(accessToken);
            return LoginUserVO.builder()
                    .userName(accessToken1.getUserInfo().get(LoginUserVO.INFO_KEY_USERNAME))
                    .userProfile(accessToken1.getUserInfo().get(LoginUserVO.INFO_KEY_USERPROFILE))
                    .userRole(accessToken1.getUserInfo().get(LoginUserVO.INFO_KEY_USERROLE))
                    .userAvatar(accessToken1.getUserInfo().get(LoginUserVO.INFO_KEY_USERAVATAR))
                    .userMailbox(accessToken1.getUserInfo().get(LoginUserVO.INFO_KEY_USEMAILBOX))
                    .id(accessToken1.getUserId())
                    .build();
        } catch (BusinessException businessException) {
            // 校验 Token 不通过时，考虑到一些接口是无需登录的，所以直接返回 null 即可
            return null;
        }
    }

}
