package com.qsj.acoj.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qsj.acoj.common.BaseResponse;
import com.qsj.acoj.common.ErrorCode;
import com.qsj.acoj.common.ResultUtils;
import com.qsj.acoj.exception.BusinessException;
import com.qsj.acoj.exception.ThrowUtils;
import com.qsj.acoj.model.dto.admin.AdminApplicationQueryRequest;
import com.qsj.acoj.model.dto.admin.AdminApplicationRequest;
import com.qsj.acoj.model.dto.admin.HandleApplicationRequest;
import com.qsj.acoj.model.entity.AdminApplication;
import com.qsj.acoj.model.vo.AdminApplicationVO;
import com.qsj.acoj.model.vo.LoginUserVO;
import com.qsj.acoj.service.AdminApplicationService;
import com.qsj.acoj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {
    
    @Resource
    private AdminApplicationService adminApplicationService;
    
    @Resource
    private UserService userService;
    
    /**
     * 申请成为管理员
     */
    @PostMapping("/apply")
    public BaseResponse<Long> applyForAdmin(@RequestBody AdminApplicationRequest request,
                                            HttpServletRequest httpRequest) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUserVO loginUser = userService.getLoginUser(httpRequest);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        long result = adminApplicationService.applyForAdmin(request, loginUser);
        return ResultUtils.success(result);
    }
    
    /**
     * 处理管理员申请（仅超级管理员可操作）
     */
    @PostMapping("/application/handle")
    public BaseResponse<Boolean> handleApplication(@RequestBody HandleApplicationRequest request,
            HttpServletRequest httpRequest) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 仅超级管理员可操作
        if (!userService.isSuperAdmin(httpRequest)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = adminApplicationService.handleApplication(request, userService.getLoginUser(httpRequest));
        return ResultUtils.success(result);
    }
    
    /**
     * 获取申请列表（仅超级管理员可查看）
     */
    @PostMapping("/application/list/page")
    public BaseResponse<Page<AdminApplicationVO>> listApplications(@RequestBody AdminApplicationQueryRequest request,
                                                                   HttpServletRequest httpRequest) {
        if (!userService.isSuperAdmin(httpRequest)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        long current = request.getCurrent();
        long size = request.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<AdminApplication> applicationPage = adminApplicationService.page(new Page<>(current, size),
                adminApplicationService.getQueryWrapper(request));
        return ResultUtils.success(adminApplicationService.getAdminApplicationVOPage(applicationPage));
    }
    
    /**
     * 获取用户申请记录
     */
    @GetMapping("/application/my")
    public BaseResponse<List<AdminApplicationVO>> getMyApplications(HttpServletRequest request) {
        LoginUserVO loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        
        // 获取用户的所有申请记录
        QueryWrapper<AdminApplication> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId())
                .orderByDesc("createTime");
        List<AdminApplication> applications = adminApplicationService.list(queryWrapper);
        
        // 转换为VO对象
        List<AdminApplicationVO> applicationVOs = applications.stream()
                .map(adminApplicationService::getAdminApplicationVO)
                .collect(Collectors.toList());
                
        return ResultUtils.success(applicationVOs);
    }
} 