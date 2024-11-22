package com.qsj.acoj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qsj.acoj.model.dto.admin.AdminApplicationQueryRequest;
import com.qsj.acoj.model.dto.admin.AdminApplicationRequest;
import com.qsj.acoj.model.dto.admin.HandleApplicationRequest;
import com.qsj.acoj.model.entity.AdminApplication;
import com.qsj.acoj.model.vo.AdminApplicationVO;
import com.qsj.acoj.model.vo.LoginUserVO;

/**
 * 管理员申请服务
 */
public interface AdminApplicationService extends IService<AdminApplication> {
    
    /**
     * 申请成为管理员
     *
     * @param request 申请信息
     * @param loginUser 当前登录用户
     * @return 申请ID
     */
    long applyForAdmin(AdminApplicationRequest request, LoginUserVO loginUser);
    
    /**
     * 处理管理员申请
     *
     * @param request 处理信息
     * @param loginUser 处理人
     * @return 是否处理成功
     */
    boolean handleApplication(HandleApplicationRequest request, LoginUserVO loginUser);

    /**
     * 获取查询条件
     *
     * @param request
     * @return
     */
    QueryWrapper<AdminApplication> getQueryWrapper(AdminApplicationQueryRequest request);

    /**
     * 获取申请视图对象
     *
     * @param adminApplication
     * @return
     */
    AdminApplicationVO getAdminApplicationVO(AdminApplication adminApplication);

    /**
     * 分页获取申请视图对象
     *
     * @param applicationPage
     * @return
     */
    Page<AdminApplicationVO> getAdminApplicationVOPage(Page<AdminApplication> applicationPage);
} 