package com.qsj.acoj.service.impl;

import static com.qsj.acoj.constant.UserConstant.USER_LOGIN_STATE;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qsj.acoj.common.ErrorCode;
import com.qsj.acoj.constant.CommonConstant;
import com.qsj.acoj.constant.TokenConstant;
import com.qsj.acoj.convert.ConvertUtils;
import com.qsj.acoj.exception.BusinessException;
import com.qsj.acoj.mapper.AccessTokenMapper;
import com.qsj.acoj.mapper.RefreshTokenMapper;
import com.qsj.acoj.mapper.UserMapper;
import com.qsj.acoj.model.dto.user.UserQueryRequest;
import com.qsj.acoj.model.entity.AccessToken;
import com.qsj.acoj.model.entity.RefreshToken;
import com.qsj.acoj.model.entity.User;
import com.qsj.acoj.model.enums.UserRoleEnum;
import com.qsj.acoj.model.vo.LoginUserVO;
import com.qsj.acoj.model.vo.UserLoginRespVO;
import com.qsj.acoj.model.vo.UserVO;
import com.qsj.acoj.service.UserService;
import com.qsj.acoj.service.UserTokenService;
import com.qsj.acoj.utils.DateUtils;
import com.qsj.acoj.utils.RedisTokenUtils;
import com.qsj.acoj.utils.SecurityFrameworkUtils;
import com.qsj.acoj.utils.SqlUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * 用户服务实现
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    UserMapper userMapper;
    @Resource
    UserTokenService userTokenService;
    @Resource
    RedisTokenUtils redisTokenUtils;
    @Resource
    AccessTokenMapper accessTokenMapper;

    @Resource
    RefreshTokenMapper refreshTokenMapper;




    @Override
    public long mySaveBatch(List<User> userList) {
        int size = userList.size();
        final int batchSize = 1000;
        final int number = size % batchSize == 0 ? size / batchSize : size / batchSize + 1;
        long sum = 0;
        for (int i = 0; i < number; i++) {
            List<User> sub = null;
            if (i == number - 1) {
                sub = ListUtil.sub(userList, i * batchSize, size);
            } else {
                sub = ListUtil.sub(userList, i * batchSize, (i + 1) * batchSize);
            }
            sum += userMapper.myInsertBatch(sub);
        }
        return sum;
    }

    @Override
    public void Test() {


//        List<User> list = new ArrayList<>();
//        for(int i = 0; i < 10100; i ++){
//            User user = new User();
//            user.setUserAccount("sdf");
//            user.setUserPassword("sfsf");
//            list.add(user);
//        }
//                // 总数
//
//                final int total = list.size();
//                // 每次导入多少条数据
//                final int batchSize = 400;
//                // 导入次数
//                final int number = total % batchSize == 0 ? total / batchSize : total / batchSize + 1;
//        CountDownLatch countDownLatch = new CountDownLatch(number);
//        ExecutorService pool = new ThreadPoolExecutor(number, number,
//                0L, TimeUnit.MILLISECONDS,
//                new LinkedBlockingQueue<Runnable>(1024), new ThreadPoolExecutor.AbortPolicy());
//        for (int i = 0; i < number; i++) {
//                    List<User> batchList = null;
//                    // 最后一批
//                    if (i == number - 1) {
//                        batchList = list.subList(i * batchSize, total);
//                    } else {
//                        batchList = list.subList(i * batchSize, (i + 1) * batchSize);
//                    }
//                    // 开始批量导入
////                    CompletableFuture<Integer> future = asynBatchAddDeviceType(batchList, countDownLatch);
//                    List<User> finalBatchList = batchList;
//           CompletableFuture.supplyAsync(() -> {
//                return saveBatch(finalBatchList);
//            }, pool).whenComplete((v, e) -> {
//                if (e == null) {
//
//                  countDownLatch.countDown();
//                } else {
//                    log.error("notice failed", e);
//                }
//            });
//            //异步通知下游系统
//
////所有任务通知成功后，更新代还通知单
//
//        }
//
//        try {
//            countDownLatch.await();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//
//    }

        List<User> list = new ArrayList<>();
        for (int i = 0; i < 101000; i++) {
            User user = new User();
            user.setUserAccount("sdf");
            user.setUserPassword("sfsf");
            list.add(user);
        }

        final int total = list.size();
        final int batchSize = 1000;
        final int number = total % batchSize == 0 ? total / batchSize : total / batchSize + 1;
        ArrayList<List<User>> lists = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            List<User> batchList = null;
            if (i == number - 1) {
                batchList = list.subList(i * batchSize, total);
            } else {
                batchList = list.subList(i * batchSize, (i + 1) * batchSize);
            }
            lists.add(batchList);

        }

        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(30,
                30,
                1,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(1500),
                new ThreadFactoryBuilder().setNamePrefix("threadPool-%d").build());


        List<CompletableFuture<Long>> completableFutureList = lists.stream()
                .map(list1 -> CompletableFuture.supplyAsync(() -> mySaveBatch(list1), threadPool))
                .collect(Collectors.toList());


        long sum = completableFutureList.stream()
                .mapToLong(CompletableFuture::join).sum();
        System.out.println("插入条数：" + sum);
        threadPool.shutdownNow();

    }


    // 指定线程池
//    @Async("threadPoolTaskExecutor")
//    public CompletableFuture<Integer> asynBatchAddDeviceType(List<User> batchList, CountDownLatch countDownLatch) {
//        try {
//            // 批量保存数据库
//            saveBatch(batchList);
//            return CompletableFuture.completedFuture(batchList.size());
//        } catch (Exception e) {
//            log.error("批量导入失败:{}", e.getMessage(), e);
//            return CompletableFuture.completedFuture(0);
//        } finally {
//            countDownLatch.countDown();
//        }
//    }


    /**
     * 盐值，混淆密码
     */
    public static final String SALT = "qsj";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword,String userMailbox) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setUserMailbox(userMailbox);
            user.setUserName(userAccount);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public UserLoginRespVO userLogin(String userAccount, String userPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        //request.getSession().setAttribute(USER_LOGIN_STATE, user);


        //创建 accessToken 和 refreshToke
        return createToken(user.getId());
    }

    private UserLoginRespVO createToken(Long userId) {
        List<RefreshToken> refreshTokens = refreshTokenMapper.selectList(Wrappers.<RefreshToken>lambdaQuery()
                .eq(RefreshToken::getUserId, userId));
        if(!refreshTokens.isEmpty()) {
            List<AccessToken> accessTokens = accessTokenMapper.getByRefreshToken(refreshTokens.get(0).getRefreshToken());
            return ConvertUtils.convert(accessTokens.get(0));
        }

        RefreshToken userRefreshToken = userTokenService.getUserRefreshToken(userId);
        AccessToken userAccessToken = userTokenService.getUserAccessToken(userRefreshToken);
        return ConvertUtils.convert(userAccessToken);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public LoginUserVO getLoginUser(HttpServletRequest request) {
        String accessToken = request.getHeader(TokenConstant.HEADER_ACCESS_TOKEN);
        if (accessToken == null) {
            throw  new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        AccessToken accessToken1 = redisTokenUtils.get(accessToken);
        if (accessToken1 == null) {
            accessToken1 = accessTokenMapper.selectByAccessToke(accessToken);
        }

        if (accessToken1 != null && DateUtils.isExpired(accessToken1.getExpiresTime())) {
            throw new BusinessException(ErrorCode.ACCESS_TOKEN_EXPIRED);
        }
        LoginUserVO loginUserVO = SecurityFrameworkUtils.getLoginUserVO();
        if (loginUserVO == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return loginUserVO;
    }

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request
     * @return
     */
    @Override
    public LoginUserVO getLoginUserPermitNull(HttpServletRequest request) {
        // 先判断是否已登录
//        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
//        User currentUser = (User) userObj;
//        if (currentUser == null || currentUser.getId() == null) {
//            return null;
//        }
//        // 从数据库查询（追求性能的话可以注释，直接走缓存）
//        long userId = currentUser.getId();
//        return this.getById(userId);
        LoginUserVO loginUserVO = SecurityFrameworkUtils.getLoginUserVO();
        return loginUserVO;
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
//        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
//        User user = (User) userObj;
//        return isAdmin(user);
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        User user = userMapper.selectById(loginUserId);
        return isAdmin(user);
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 用户注销
     *
     * @param
     */
    @Override
    public void userLogout(String accessToken) {
        userTokenService.removeToken(accessToken);
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = LoginUserVO.builder().build();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public UserLoginRespVO getUserLoginRespVo(User user) {
        if (user == null) {
            return null;
        }
        UserLoginRespVO userLoginRespVO = new UserLoginRespVO();
        BeanUtils.copyProperties(user, userLoginRespVO);
        return userLoginRespVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String unionId = userQueryRequest.getUnionId();
        String mpOpenId = userQueryRequest.getMpOpenId();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(unionId), "unionId", unionId);
        queryWrapper.eq(StringUtils.isNotBlank(mpOpenId), "mpOpenId", mpOpenId);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public UserLoginRespVO refreshToken(String refreshToken) {
        return ConvertUtils.convert(userTokenService.refreshToken(refreshToken));
    }

    @Override
    public boolean isSuperAdmin(HttpServletRequest request) {
        LoginUserVO loginUser = this.getLoginUser(request);
        return loginUser != null && UserRoleEnum.SUPER_ADMIN.getValue().equals(loginUser.getUserRole());
    }

}
