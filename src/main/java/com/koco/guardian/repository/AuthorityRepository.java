package com.koco.guardian.repository;

import com.koco.guardian.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    boolean existsByName(String name);
    Optional<Authority> findByName(String name);
}
