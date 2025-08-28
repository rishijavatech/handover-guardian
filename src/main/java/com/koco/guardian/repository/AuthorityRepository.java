package com.koco.guardian.repository;

import com.koco.guardian.entity.Authority;
import com.koco.guardian.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

}
