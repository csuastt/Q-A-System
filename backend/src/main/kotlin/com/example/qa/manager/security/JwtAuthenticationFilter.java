package com.example.qa.manager.security;

import com.example.qa.manager.constants.SecurityConstants;
import com.example.qa.manager.exchange.AuthenticationSuccessResponse;
import com.example.qa.manager.model.AppManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;

        setFilterProcessesUrl(SecurityConstants.AUTH_LOGIN_URL);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        var managername = request.getParameter("managername");
        var password = request.getParameter("password");
        var authenticationToken = new UsernamePasswordAuthenticationToken(managername, password);

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain, Authentication authentication) {
        var manager = ((AppManager) authentication.getPrincipal());

        var roles = manager.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        var signingKey = SecurityConstants.JWT_SECRET.getBytes();

        var token = Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, signingKey)
                .setHeaderParam("typ", SecurityConstants.TOKEN_TYPE)
                .setIssuer(SecurityConstants.TOKEN_ISSUER)
                .setAudience(SecurityConstants.TOKEN_AUDIENCE)
                .setSubject(manager.getManagername())
                .setExpiration(new Date(System.currentTimeMillis() + 864000000))
                .claim("rol", roles)
                .compact();

        response.addHeader(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + token);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

        try {
            String json = ow.writeValueAsString(new AuthenticationSuccessResponse(token, manager));
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(json);
            response.getWriter().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
