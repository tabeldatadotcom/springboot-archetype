package com.maryanto.dimas.archetype.oauth;

import com.maryanto.dimas.archetype.oauth.repository.JdbcTokenStoreCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@Configuration
@EnableResourceServer
@Order(2)
public class OauthResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Autowired
    private JdbcTokenStoreCustom tokenStore;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("oauth2-resource")
                .tokenStore(tokenStore);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.cors().disable()
                .csrf().disable();

        http.antMatcher("/api/**")
                .authorizeRequests()
                .antMatchers("/oauth/**",
                        "/swagger-ui.html",
                        "/swagger-resources/**",
                        "/v2/api-docs",
                        "/webjars/**",
                        "/static/**").permitAll()
                .anyRequest().authenticated();
    }
}
