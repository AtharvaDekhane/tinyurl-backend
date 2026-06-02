package com.tinyurl.repository;

import com.tinyurl.entity.Url;
import com.tinyurl.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {

    Optional<Url> findByShortCode(String shortCode);
    long countByUser(User user);
    List<Url> findByUser(User user);
}