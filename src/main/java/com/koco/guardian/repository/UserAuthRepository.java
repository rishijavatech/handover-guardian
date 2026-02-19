package com.koco.guardian.repository;

import com.koco.guardian.entity.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {
    Optional<UserAuth> findByEmailAndIsDeletedFalse(String email);
    Optional<UserAuth> findByEmail(String email);

    Optional<UserAuth> findByUserId(String userId);

    Optional<UserAuth> findByUserIdAndEmailAndIsDeletedFalse(String userId, String email);
}
