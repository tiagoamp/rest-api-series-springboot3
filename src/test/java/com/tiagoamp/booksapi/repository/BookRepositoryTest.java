package com.tiagoamp.booksapi.repository;

import com.tiagoamp.booksapi.TestHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private TestEntityManager em;  // entity manager for tests

    @Autowired
    private BookRepository repo; // class under test


    @Nested
    @DisplayName("Find by Book Title")
    class FindByTitleTest {

        @Test
        @DisplayName("When there is no book with given title, should return empty")
        void findByTitle_notFound() {
            // given
            List<BookEntity> books = TestHelper.getBooksEntityWithNoIdMock();
            books.forEach(b -> em.persist(b));
            // when
            Optional<BookEntity> result = repo.findByTitle("NOT EXISTING TITLE");
            // then
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("When there is a registered book with given title, should return optional with the book")
        void findByTitle() {
            // given
            List<BookEntity> books = TestHelper.getBooksEntityWithNoIdMock();
            books.forEach(b -> em.persist(b));
            int index = books.size()-2;
            String title = books.get(index).getTitle();
            // when
            Optional<BookEntity> result = repo.findByTitle(title);
            // then
            assertFalse(result.isEmpty());
            assertEquals(title, result.get().getTitle());
            assertEquals(books.get(index).getId(), result.get().getId());
        }

    }

}