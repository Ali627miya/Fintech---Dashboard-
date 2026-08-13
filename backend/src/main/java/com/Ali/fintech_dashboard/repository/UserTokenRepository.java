package com.Ali.fintech_dashboard.repository;

import com.Ali.fintech_dashboard.entity.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
}
