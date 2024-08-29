package com.qsj.acoj.model.vo;

import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * 已登录用户视图（脱敏）
 *
 *
 **/
@Data
@Builder
public class LoginUserVO implements Serializable {
    public static String INFO_KEY_USERNAME = "userName";
    public static String INFO_KEY_USERAVATAR = "userAvatar";
    public static String INFO_KEY_USERPROFILE = "userProfile";
    public static String INFO_KEY_USERROLE = "userRole";
    public static String INFO_KEY_USEMAILBOX = "userMailbox";

    /**
     * 用户 id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    /**
     * 用户邮箱
     */
    private  String userMailbox;

    private static final long serialVersionUID = 1L;
}