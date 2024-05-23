package com.qsj.acoj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qsj.acoj.model.entity.User;
import com.qsj.acoj.mybatis.BaseMapperX;
import org.apache.ibatis.annotations.Delete;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

/**
 * 用户数据库操作
 *
 *
 */
public interface UserMapper extends BaseMapperX<User> {
    @Delete("delete from user where userAccount = 'sdf'")
    void deleteTest();

    long myInsertBatch(List<User> userlist);

}




