package com.qsj.acoj.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) throws Exception {
        //忽略对以下地址的安全校验 加入你不想security校验的接口即可
        web.ignoring().antMatchers("**");
    }
}
