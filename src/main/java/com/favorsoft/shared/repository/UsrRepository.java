package com.favorsoft.shared.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.favorsoft.shared.entity.Usr;

@Repository
public interface UsrRepository extends JpaRepository<Usr, String> {
    Usr findByLoginId(String loginId);
}
