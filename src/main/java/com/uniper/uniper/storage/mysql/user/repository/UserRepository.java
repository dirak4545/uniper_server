package com.uniper.uniper.storage.mysql.user.repository;

import com.uniper.uniper.storage.mysql.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
