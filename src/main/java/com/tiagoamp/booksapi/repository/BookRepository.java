package com.tiagoamp.booksapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<BookEntity, Integer> {

    Optional<BookEntity> findByTitle(String title);

}
