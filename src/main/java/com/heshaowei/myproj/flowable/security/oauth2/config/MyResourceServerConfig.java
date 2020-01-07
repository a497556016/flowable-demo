package com.heshaowei.myproj.flowable.security.oauth2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.web.cors.reactive.CorsUtils;

/**
 * 资源服务器
 * Created by Fant.J.
 */
@Configuration
@EnableResourceServer
public class MyResourceServerConfig extends ResourceServerConfigurerAdapter {

//    @Autowired
//    private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;
//    @Autowired
//    private MyAuthenticationFailHandler myAuthenticationFailHandler;
    @Override
    public void configure(HttpSecurity http) throws Exception {
        //表单登录 方式
//        http.formLogin()
//                .loginPage("/authentication/require")
//                //登录需要经过的url请求
//                .loginProcessingUrl("/authentication/form")
//                .successHandler(myAuthenticationSuccessHandler)
//                .failureHandler(myAuthenticationFailHandler);

        //允许iframe嵌入访问
        http.headers().frameOptions().disable();

        http
                //配置那些请求需要权限验证
                .authorizeRequests()
//                .antMatchers("/api/**")
//                .authenticated()

                //配置不需要认证的请求
                .antMatchers("/oauth/token", "/", "/lib/**", "/app/rest/admin/process-instances/*/model-json", "/app/rest/admin/process-instances/*/history-model-json", "/app/rest/admin/process-definitions/*/model-json")
                .permitAll()

                //其他请求都需要权限
                .anyRequest()
                .authenticated()

                .and()

                //关闭跨站请求防护
                .cors().and().csrf().disable();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
//        resources.resourceId("myResourceServer").stateless(true);
    }
}