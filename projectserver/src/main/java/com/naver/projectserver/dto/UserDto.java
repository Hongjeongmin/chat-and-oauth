package com.naver.projectserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data @Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class UserDto {
	String username;
	String pwd;
	String name;
	String phone;
	String email;
}
