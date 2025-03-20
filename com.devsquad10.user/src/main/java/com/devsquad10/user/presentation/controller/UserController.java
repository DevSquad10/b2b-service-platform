package com.devsquad10.user.presentation.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.devsquad10.user.application.dto.UserLoginRequestDto;
import com.devsquad10.user.application.dto.UserRequestDto;
import com.devsquad10.user.application.dto.UserResponseDto;
import com.devsquad10.user.application.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping("/signup")
	public ResponseEntity<?> signup(@RequestBody UserRequestDto requestDto) {
		userService.signup(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 완료되었습니다.");
	}

	@PostMapping("/signIn")
	public ResponseEntity<?> signIn(@RequestBody UserLoginRequestDto requestDto, HttpServletResponse res) {
		String token = userService.signIn(requestDto);
		userService.addJwtToHeader(token, res);

		return ResponseEntity.status(HttpStatus.OK)
			.body("로그인 성공");
	}

	@GetMapping("{id}")
	public ResponseEntity<?> getUserInfo(@RequestPart UUID id) {
		UserResponseDto userInfo = userService.getUserInfo(id);
		return ResponseEntity.status(HttpStatus.OK)
			.body("유저 정보");
	}
}
