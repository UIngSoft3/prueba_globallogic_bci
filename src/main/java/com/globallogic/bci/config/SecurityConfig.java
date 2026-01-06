package com.globallogic.bci.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the application.
 * Disables H2 Console authentication to allow direct access.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Configure HTTP security to allow H2 Console access without authentication.
     * @param http HttpSecurity to configure
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/h2-console", "/h2-console/**").permitAll()
                .antMatchers("/sign-up", "/login").permitAll()
                .anyRequest().permitAll()
            .and()
            .csrf()
                .ignoringAntMatchers("/h2-console/**", "/sign-up", "/login")
            .and()
            .headers()
                .frameOptions().disable();
    }
}
