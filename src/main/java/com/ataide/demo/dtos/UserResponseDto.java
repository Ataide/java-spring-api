package com.ataide.demo.dtos;

import lombok.Data;

@Data
public class UserResponseDto {

  public UserResponseDto(Long id, String name, String token) {
    this.id = id;
    this.name = name;
    this.token = token;
  }
  
  public Long id;
  public String name;
  public String token;
}
