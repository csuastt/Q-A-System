package com.example.qa.manager.configuration;


import com.example.qa.manager.security.JwtAuthenticationFilter;
import com.example.qa.manager.security.JwtAuthorizationFilter;
import com.example.qa.manager.service.ManagerService;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
class SpringSecurityConfiguration {

    @Configuration
    @Order(1)
    public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Autowired
        private ManagerService managerService;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.cors().and()
                    .csrf().disable()
                    .authorizeRequests()
                    .antMatchers(HttpMethod.POST, "/api/managers").permitAll()
                    .antMatchers(HttpMethod.POST, "/api/manager/login").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                    .addFilter(new JwtAuthorizationFilter(authenticationManager()))
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .logout()
                    .logoutUrl("/api/manager/logout")
                    .logoutSuccessHandler(new LogoutSuccessHandler() {
                        @Override
                        public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
                            httpServletResponse.getWriter().write("{\n " +
                                    "\"code\" : \"200\", \n" +
                                    " \"message\" : \"Success\"\n" +
                                    "}");
                        }
                    });


        }

        @Override
        public void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(managerService)
                    .passwordEncoder(passwordEncoder());
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
            final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());

            return source;
        }
    }
}
