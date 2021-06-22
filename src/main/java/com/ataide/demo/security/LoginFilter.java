package com.ataide.demo.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
@Order(1)  
public class LoginFilter implements Filter {
 
  @Autowired
  private JwtProvider jwtProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

      HttpServletResponse httpResponse = (HttpServletResponse)response;
      HttpServletRequest httpRequest = (HttpServletRequest)request;           
      
      if(httpRequest.getMethod().equals("OPTIONS")) {
        chain.doFilter(request, response);
        return;
      }
      //Para exemplificar as duas rotas permitidas sem credenciais. /login /register
      if (httpRequest.getServletPath().startsWith("/login")) {
          // Pode entrar sem token         
          chain.doFilter(request, response);
          return;
      }
      if (httpRequest.getServletPath().startsWith("/")) {
        // Pode entrar sem token     
        chain.doFilter(request, response);
        return;
    }

      if (httpRequest.getServletPath().startsWith("/register")) {
        // Pode entrar sem token
        chain.doFilter(request, response);
        return;
    }

      String token = httpRequest.getHeader("Authorization");

      if (token == null) {
          httpResponse.sendError(HttpStatus.UNAUTHORIZED.value());
          return;
      }

      try {

        String jwt = token.substring(7, token.length());
        DecodedJWT decodedJwt = jwtProvider.decodeToken(jwt);
        Integer userId = decodedJwt.getClaim("userId").asInt();
        httpRequest.setAttribute("userId", userId);

        // chamada autenticada
        chain.doFilter(request, response);

      } catch (JWTVerificationException ex) {
          httpResponse.sendError(HttpStatus.UNAUTHORIZED.value());
          return;
      }
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

}