package com.devsquad10.user.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.devsquad10.user.domain.model.User;

public interface UserRepository {
	Optional<Object> findByUsername(String username);

	User save(User user);

	Optional<Object> findByIdAndDeletedAtIsNull(UUID id);
}
