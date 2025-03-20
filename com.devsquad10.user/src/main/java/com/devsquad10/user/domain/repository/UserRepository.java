package com.devsquad10.user.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.devsquad10.user.domain.model.User;
import com.devsquad10.user.domain.model.UserRoleEnum;

public interface UserRepository {
	Optional<Object> findByUsername(String username);

	User save(User user);

	Optional<Object> findByIdAndDeletedAtIsNull(UUID id);

	Page<User> searchUser(UserRoleEnum userRoleEnum, String category, int page, int size, String sort, String order);

	Optional<Object> findByEmail(String email);

	Optional<Object> findBySlackId(String slackId);
}
