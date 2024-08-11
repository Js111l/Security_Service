package com.ecom.security_service.dao;

import com.ecom.security_service.dao.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    @Query(value = " select t from VerificationToken t where t.uuid = :uuid ")
    Optional<VerificationToken> findTokenByUUID(@Param("uuid") String token);
}
