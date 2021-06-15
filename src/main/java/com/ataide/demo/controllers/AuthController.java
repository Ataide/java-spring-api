package com.ataide.demo.controllers;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.ataide.demo.dtos.UserResponseDto;
import com.ataide.demo.models.User;
import com.ataide.demo.repositories.UserRepository;
import com.ataide.demo.security.JwtProvider;
import org.springframework.http.HttpStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private JwtProvider jwtProvider;

  @CrossOrigin
  @RequestMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  public UserResponseDto login(@RequestBody User userBody, HttpServletResponse response) throws IOException {
    try {
      User userExists = userRepository.findByEmail(userBody.getEmail());
     
      if(!userExists.getPassword().equals(userBody.getPassword())) {
        throw new Exception();        
      }
      String token = jwtProvider.createToken(userExists.getId().toString());   
      UserResponseDto userResponse = new UserResponseDto(userExists.getId(),userExists.getName(), token);
      // Cookie cookie = new Cookie("token", token);   
      // cookie.setPath("/");
      // cookie.setMaxAge(60 * 30); // 30 minutos
      // response.addCookie(cookie);
      // response.addHeader("Autorization", "Bearer "+token);
      return userResponse;
      
    } catch (Exception e) {      
      response.sendError(HttpStatus.BAD_REQUEST.value(), "wrong password/email"); 
      return null;
    }        
  }
  @CrossOrigin
  @RequestMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public void register(@RequestBody User user, HttpServletResponse response ) throws IOException {
    if(!userRepository.existsByEmail(user.getEmail())){
      userRepository.save(user);
      String token = jwtProvider.createToken(user.getId().toString());     
      Cookie cookie = new Cookie("token", token);   
      cookie.setPath("/");
      cookie.setMaxAge(60 * 30); // 30 minutos
      response.addCookie(cookie);        
    } else {
      response.sendError(HttpStatus.BAD_REQUEST.value(), "email already in use");     
    }
  }
  @CrossOrigin
  @RequestMapping("/users")
  public List<User> listUsers() {
    return userRepository.findAll();
  }
  
}
