package com.Ali.fintech_dashboard.repository;

import com.Ali.fintech_dashboard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
