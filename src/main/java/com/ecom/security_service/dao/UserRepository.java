package com.ecom.security_service.dao;

import com.ecom.security_service.dao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = " select * from \"user\" u where u.email = :email ", nativeQuery = true)
    Optional<User> findByEmail(@Param("email") String email);

}
