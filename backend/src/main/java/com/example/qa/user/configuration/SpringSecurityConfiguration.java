package com.example.qa.user.configuration;

import com.example.qa.user.security.JwtAuthenticationFilter;
import com.example.qa.user.security.JwtAuthorizationFilter;
import com.example.qa.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SpringSecurityConfiguration {

    @Configuration
    @Order(1)
    public static class SecurityConfiguration extends WebSecurityConfigurerAdapter {

        private final UserService userService;
        private final ObjectMapper objectMapper;

        @Autowired
        public SecurityConfiguration(UserService userService, ObjectMapper objectMapper) {
            this.userService = userService;
            this.objectMapper = objectMapper;
        }

        /**
         * Control the permission of all api path, register and login is open for all
         * While other path demand JWT authentication
         * @param http HTTP Request
         * @throws Exception Internal Exception
         */
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.cors().and()
                    .csrf().disable()
                    .authorizeRequests()
                    .antMatchers(HttpMethod.POST,"/api/users").permitAll()
                    .antMatchers(HttpMethod.POST,"/api/user/login").permitAll()
                    .regexMatchers(HttpMethod.GET, "/api/users/[0-9]*/basic").permitAll()
                    .regexMatchers(HttpMethod.GET,"/api/users?.*answerer=true.*").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .addFilter(new JwtAuthenticationFilter(authenticationManager(), objectMapper))
                    .addFilter(new JwtAuthorizationFilter(authenticationManager()))
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .logout()
                    .logoutUrl("/api/user/logout")
                    .logoutSuccessHandler((httpServletRequest, httpServletResponse, authentication) -> httpServletResponse.getWriter().write("""
                            {
                             "code" : "200",\s
                             "message" : "Success"
                            }"""));
        }

        @Override
        public void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userService)
                    .passwordEncoder(passwordEncoder());
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }
}

