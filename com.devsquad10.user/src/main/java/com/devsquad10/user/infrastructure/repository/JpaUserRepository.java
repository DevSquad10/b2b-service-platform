package com.devsquad10.user.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.devsquad10.user.domain.model.User;
import com.devsquad10.user.domain.repository.UserRepository;

@Repository
public interface JpaUserRepository
	extends JpaRepository<User, UUID>, QuerydslPredicateExecutor<User>, UserRepository {

}
