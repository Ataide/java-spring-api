package com.ataide.demo.controllers;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.ataide.demo.dtos.UserResponseDto;
import com.ataide.demo.models.User;
import com.ataide.demo.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class MainController {
  
  @Autowired
  private UserRepository userRepository;  

  @RequestMapping("/")
  @ResponseStatus(HttpStatus.OK)
  public String checkApi() {
   return "Api ready";
  }
  
  @CrossOrigin
  @RequestMapping("/users")
  public List<User> list() {
    return userRepository.findAll();
  }

  @GetMapping("/users/{id}")
  public ResponseEntity<?> findOne(@PathVariable long id) {
    return userRepository.findById(id).map(result -> {
      return ResponseEntity.ok().body(result);
    }).orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/users")
  public ResponseEntity<?> edit(@RequestBody User userBody, HttpServletResponse response) {
     return userRepository.findById(userBody.getId()).map( result -> {
        if(userBody.getName() != null) {    
          result.setName(userBody.getName());
        }
        if(userBody.getEmail() != null) {
          result.setEmail(userBody.getEmail());          
        }
        if(userBody.getPassword() != null) {
          result.setPassword(userBody.getPassword());
        }         
        User updated = userRepository.save(result);

        return ResponseEntity.ok().body(updated);        
      }).orElse(ResponseEntity.notFound().build());
  }  

  @DeleteMapping("/users/{id}")
  public ResponseEntity<?> delete(@PathVariable Long id) {
    return userRepository.findById(id).map(result -> {
      userRepository.deleteById(id);
      return ResponseEntity.ok().build();
    }).orElse(ResponseEntity.notFound().build());
  }

  
}
