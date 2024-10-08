package com.qsj.acoj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qsj.acoj.model.entity.AccessToken;
import com.qsj.acoj.mybatis.BaseMapperX;
import org.mapstruct.Mapper;

import java.util.List;

public interface AccessTokenMapper extends BaseMapperX<AccessToken> {
  default AccessToken selectByAccessToke(String accessToken) {
      return selectOne(AccessToken::getAccessToken,accessToken);
    }

    default List<AccessToken> selectListByRefreshToken(String refreshToken) {
      return selectList(AccessToken::getRefreshToken,refreshToken);
    }

    List<AccessToken> getByRefreshToken(String refreshToken);
}
