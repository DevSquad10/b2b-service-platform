package com.devsquad10.user.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.devsquad10.user.application.dto.UserRequestDto;
import com.devsquad10.user.domain.model.User;
import com.devsquad10.user.domain.model.UserRoleEnum;
import com.devsquad10.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${service.jwt.master-key}")
	private String MASTER_KEY;

	public void signup(UserRequestDto requestDto) {
		duplicationCheck(requestDto.getUsername(), requestDto.getEmail());

		if (requestDto.getRole().equals(UserRoleEnum.MASTER)) {
			checkMasterKey(requestDto.getMasterKey());
		}
		String password = passwordEncoder.encode(requestDto.getPassword());
		userRepository.save(new User(requestDto, password));
	}

	private void duplicationCheck(String username, String email) {
		userRepository.findByUsername(username).ifPresent((m) -> {
			throw new IllegalArgumentException("이미 존재하는 사용자 이름입니다.");
		});
	}

	private void checkMasterKey(String masterKey) {
		if (!masterKey.equals(MASTER_KEY)) {
			throw new IllegalArgumentException("관리자 키가 일치하지 않습니다.");
		}
	}

}