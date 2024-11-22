package com.qsj.acoj.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qsj.acoj.common.ErrorCode;
import com.qsj.acoj.exception.BusinessException;
import com.qsj.acoj.mapper.AdminApplicationMapper;
import com.qsj.acoj.model.dto.admin.AdminApplicationQueryRequest;
import com.qsj.acoj.model.dto.admin.AdminApplicationRequest;
import com.qsj.acoj.model.dto.admin.HandleApplicationRequest;
import com.qsj.acoj.model.entity.AdminApplication;
import com.qsj.acoj.model.entity.User;
import com.qsj.acoj.model.enums.AdminApplicationStatusEnum;
import com.qsj.acoj.model.enums.UserRoleEnum;
import com.qsj.acoj.model.vo.AdminApplicationVO;
import com.qsj.acoj.model.vo.LoginUserVO;
import com.qsj.acoj.service.AdminApplicationService;
import com.qsj.acoj.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminApplicationServiceImpl extends ServiceImpl<AdminApplicationMapper, AdminApplication>
        implements AdminApplicationService {
    
    @Resource
    private UserService userService;
    
    @Override
    public long applyForAdmin(AdminApplicationRequest request, LoginUserVO loginUser) {
        // 校验是否已经是管理员
        if (UserRoleEnum.ADMIN.getValue().equals(loginUser.getUserRole()) ||
                UserRoleEnum.SUPER_ADMIN.getValue().equals(loginUser.getUserRole())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "您已经是管理员");
        }
        
        // 校验是否有待处理的申请
        QueryWrapper<AdminApplication> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId())
                .eq("status", AdminApplicationStatusEnum.PENDING.getValue())
                .eq("isDelete", 0);
        if (this.count(queryWrapper) > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "您已有待处理的申请");
        }
        
        // 创建申请
        AdminApplication application = new AdminApplication();
        application.setUserId(loginUser.getId());
        application.setReason(request.getReason());
        application.setStatus(AdminApplicationStatusEnum.PENDING.getValue());
        boolean save = this.save(application);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "申请提交失败");
        }
        return application.getId();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleApplication(HandleApplicationRequest request, LoginUserVO loginUser) {
        // 获取申请信息
        AdminApplication application = this.getById(request.getApplicationId());
        if (application == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        // 校验状态
        if (application.getStatus() != AdminApplicationStatusEnum.PENDING.getValue()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该申请已处理");
        }
        
        // 更新申请状态
        application.setStatus(request.getStatus());
        application.setResult(request.getResult());
        application.setHandleUserId(loginUser.getId());
        
        // 如果通过申请，更新用户角色
        if (request.getStatus() == 1) {
            User user = userService.getById(application.getUserId());
            user.setUserRole(UserRoleEnum.ADMIN.getValue());
            userService.updateById(user);
        }
        
        return this.updateById(application);
    }

    @Override
    public QueryWrapper<AdminApplication> getQueryWrapper(AdminApplicationQueryRequest request) {
        QueryWrapper<AdminApplication> queryWrapper = new QueryWrapper<>();
        if (request == null) {
            return queryWrapper;
        }
        Integer status = request.getStatus();
        queryWrapper.eq(status != null, "status", status);
        queryWrapper.eq("isDelete", 0);
        queryWrapper.orderByDesc("createTime");
        return queryWrapper;
    }

    @Override
    public AdminApplicationVO getAdminApplicationVO(AdminApplication adminApplication) {
        if (adminApplication == null) {
            return null;
        }
        AdminApplicationVO vo = new AdminApplicationVO();
        BeanUtils.copyProperties(adminApplication, vo);

        // 设置申请用户信息
        Long userId = adminApplication.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            vo.setUserVO(userService.getUserVO(user));
        }
        
        // 设置处理人信息
        Long handleUserId = adminApplication.getHandleUserId();
        if (handleUserId != null && handleUserId > 0) {
            User handleUser = userService.getById(handleUserId);
            vo.setHandleUserVO(userService.getUserVO(handleUser));
        }
        
        // 设置状态（会自动设置状态文本）
        vo.setStatus(adminApplication.getStatus());
        
        return vo;
    }

    @Override
    public Page<AdminApplicationVO> getAdminApplicationVOPage(Page<AdminApplication> applicationPage) {
        List<AdminApplication> applicationList = applicationPage.getRecords();
        Page<AdminApplicationVO> adminApplicationVOPage = new Page<>(applicationPage.getCurrent(), applicationPage.getSize(),
                applicationPage.getTotal());
        if (CollUtil.isEmpty(applicationList)) {
            return adminApplicationVOPage;
        }
        List<AdminApplicationVO> adminApplicationVOList = applicationList.stream()
                .map(this::getAdminApplicationVO)
                .collect(Collectors.toList());
        adminApplicationVOPage.setRecords(adminApplicationVOList);
        return adminApplicationVOPage;
    }
} 