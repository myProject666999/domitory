package com.example.domitory.config;

import com.example.domitory.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
    
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
                .antMatchers("/login", "/css/**", "/js/**", "/images/**", "/layui/**").permitAll()
                .antMatchers("/api/user/register").permitAll()
                .antMatchers("/api/user/**").hasAnyRole("STUDENT", "DORM_MANAGER", "LOGISTICS")
                .antMatchers("/api/building/**").hasAnyRole("DORM_MANAGER", "LOGISTICS")
                .antMatchers("/api/room/**").hasAnyRole("DORM_MANAGER", "LOGISTICS")
                .antMatchers("/api/repair/**").hasAnyRole("STUDENT", "LOGISTICS")
                .antMatchers("/api/bill/**").hasAnyRole("STUDENT", "DORM_MANAGER", "LOGISTICS")
                .antMatchers("/api/account/**").hasRole("LOGISTICS")
                .antMatchers("/api/allocation/**").hasAnyRole("DORM_MANAGER", "LOGISTICS")
                .antMatchers("/api/excel/**").hasAnyRole("DORM_MANAGER", "LOGISTICS")
                .anyRequest().authenticated()
            .and()
            .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/api/login")
                .successHandler(authenticationSuccessHandler())
                .failureHandler(authenticationFailureHandler())
                .permitAll()
            .and()
            .logout()
                .logoutUrl("/api/logout")
                .logoutSuccessHandler(logoutSuccessHandler())
                .permitAll()
            .and()
            .sessionManagement()
                .maximumSessions(1)
                .expiredUrl("/login?expired");
    }
    
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            out.write(new ObjectMapper().writeValueAsString(Result.success("登录成功")));
            out.flush();
            out.close();
        };
    }
    
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            PrintWriter out = response.getWriter();
            String message = "登录失败";
            if (exception.getMessage().contains("Bad credentials")) {
                message = "用户名或密码错误";
            } else if (exception.getMessage().contains("User is disabled")) {
                message = "账户已被禁用";
            }
            out.write(new ObjectMapper().writeValueAsString(Result.error(message)));
            out.flush();
            out.close();
        };
    }
    
    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return (request, response, authentication) -> {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            out.write(new ObjectMapper().writeValueAsString(Result.success("退出成功")));
            out.flush();
            out.close();
        };
    }
}
