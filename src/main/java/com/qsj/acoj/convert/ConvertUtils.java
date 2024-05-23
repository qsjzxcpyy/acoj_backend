package com.qsj.acoj.convert;

import com.qsj.acoj.model.entity.AccessToken;
import com.qsj.acoj.model.vo.UserLoginRespVO;
import org.springframework.beans.BeanUtils;

public class ConvertUtils {
    public static UserLoginRespVO convert(AccessToken accessToken) {
        UserLoginRespVO userLoginRespVO = new UserLoginRespVO();
        BeanUtils.copyProperties(accessToken, userLoginRespVO);
        return userLoginRespVO;
    }
}
