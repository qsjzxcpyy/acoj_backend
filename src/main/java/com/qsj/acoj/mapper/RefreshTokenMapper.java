package com.qsj.acoj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qsj.acoj.model.entity.RefreshToken;
import com.qsj.acoj.mybatis.BaseMapperX;
import org.mapstruct.Mapper;

public interface RefreshTokenMapper extends BaseMapperX<RefreshToken> {
    default int  deleteByToken(String refreshToken) {
       return delete(RefreshToken::getRefreshToken,refreshToken);
    }
}
