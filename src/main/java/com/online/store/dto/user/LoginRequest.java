package com.online.store.dto.user;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
	private String email;
	private String password;
}
