package com.ataide.demo.security;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

  private String secretKey = "padawan";

  public String createToken(String userId) {
    String token = JWT.create()
      .withClaim("userId", userId)
      .sign(Algorithm.HMAC256(this.secretKey));
    return token;
  }

  public DecodedJWT decodeToken(String token) {
    return JWT.require(Algorithm.HMAC256(secretKey))
                .build()
                .verify(token);    
  }  
   
}
