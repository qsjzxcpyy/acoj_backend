package com.qsj.acoj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qsj.acoj.model.dto.user.UserQueryRequest;
import com.qsj.acoj.model.dto.user.UserRegisterRequest;
import com.qsj.acoj.model.entity.User;
import com.qsj.acoj.model.vo.LoginUserVO;
import com.qsj.acoj.model.vo.UserLoginRespVO;
import com.qsj.acoj.model.vo.UserVO;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务
 *
 *
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword,String userMailbox);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @return 脱敏后的用户信息
     */
    UserLoginRespVO userLogin(String userAccount, String userPassword);


    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    LoginUserVO getLoginUser(HttpServletRequest request);

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request
     * @return
     */
    LoginUserVO getLoginUserPermitNull(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    boolean isAdmin(User user);

    /**
     * 用户注销
     *
     *
     * @return
     */
    void userLogout(String accessToken);

    /**
     * 获取脱敏的已登录用户信息
     *
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获得登录返回信息
     */
    UserLoginRespVO getUserLoginRespVo(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param userList
     * @return
     */
    List<UserVO> getUserVO(List<User> userList);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);
    void Test();
    long mySaveBatch(List<User> userList);

    UserLoginRespVO refreshToken(String refreshToken);

    /**
     * 判断是否为超级管理员
     *
     * @param request
     * @return
     */
    boolean isSuperAdmin(HttpServletRequest request);

}
