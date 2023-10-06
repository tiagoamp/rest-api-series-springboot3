package com.tiagoamp.booksapi.service;

import com.tiagoamp.booksapi.TestHelper;
import com.tiagoamp.booksapi.exception.ResourceAlreadyExistsException;
import com.tiagoamp.booksapi.exception.ResourceNotFoundException;
import com.tiagoamp.booksapi.model.Book;
import com.tiagoamp.booksapi.repository.BookGatewayRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BooksServiceTest {

    @Mock
    private BookGatewayRepository repo;

    @InjectMocks
    private BooksService service;


    @Nested
    class FindAllTests {

        @Test
        @DisplayName("When no books registered, should return empty list")
        void findAllBooks_emptyList() {
            Mockito.when(repo.findAll(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString()))
                    .thenReturn(new ArrayList<>());
            List<Book> result = service.findBooks(10, 0, "title", "ASC");
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("When there are books registered, should return list")
        void findAllBooks_resultList() {
            // given
            var books = TestHelper.getBooksMock();
            Mockito.when(repo.findAll(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString()))
                    .thenReturn(books);
            // when
            List<Book> result = service.findBooks(10, 0, "title", "ASC");
            // then
            assertFalse(result.isEmpty());
            assertEquals(books.size(), result.size());
        }

    }

    @Nested
    class FindBookByIdTests {

        @Test
        @DisplayName("When id does not exist, should throw exception")
        void findBookById_exception() {
            Mockito.when(repo.find(Mockito.anyInt())).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> service.findBookById(1));
        }

        @Test
        @DisplayName("When id exists, should return result")
        void findBookById_result() {
            // given
            var book = TestHelper.getBookMock();
            Mockito.when(repo.find(Mockito.anyInt())).thenReturn(Optional.of(book));
            // when
            Book result = service.findBookById(book.getId());
            // then
            assertEquals(book.getId(), result.getId());
        }

    }

    @Nested
    class CreateBookTests {

        @Test
        @DisplayName("When book already registered, should throw exception")
        void createBook_exception() {
            var book = TestHelper.getBookMock();
            Mockito.when(repo.findBookByTitle(Mockito.anyString())).thenReturn(Optional.of(book));
            assertThrows(ResourceAlreadyExistsException.class, () -> service.createBook(book));
        }

        @Test
        @DisplayName("When book not registered, should insert book")
        void createBook() {
            // given
            var book = TestHelper.getBookMock();
            Mockito.when(repo.findBookByTitle(Mockito.anyString())).thenReturn(Optional.empty());
            Mockito.when(repo.save(Mockito.any(Book.class))).thenReturn(book);
            // when
            Book result = service.createBook(book);
            // then
            assertEquals(book.getId(), result.getId());
        }

    }

    @Nested
    class UpdateBookTests {

        @Test
        @DisplayName("When book does not exist, should throw exception")
        void updateBook_exception() {
            Mockito.when(repo.find(Mockito.anyInt())).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> service.findBookById(1));
        }

        @Test
        @DisplayName("When book exists, should update values")
        void updateBook() {
            // given
            var book = TestHelper.getBookMock();
            Mockito.when(repo.find(Mockito.anyInt())).thenReturn(Optional.of(book));
            Mockito.when(repo.update(Mockito.any(Book.class))).thenReturn(book);
            // when
            Book result = service.updateBook(book);
            // then
            assertEquals(book.getId(), result.getId());
        }

    }

    @Nested
    class DeleteBookTests {

        @Test
        @DisplayName("When book does not exist, should throw exception")
        void deleteBook_exception() {
            Mockito.when(repo.find(Mockito.anyInt())).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> service.deleteBook(1));
        }

        @Test
        @DisplayName("When book exists, should delete book")
        void deleteBook() {
            var book = TestHelper.getBookMock();
            Mockito.when(repo.find(Mockito.anyInt())).thenReturn(Optional.of(book));
            assertDoesNotThrow(() -> service.deleteBook(book.getId()));
        }

    }

    @Nested
    class FindReviewsOfBookTests {

        @Test
        @DisplayName("When book does not exist, should throw exception")
        void findReviewsOfBook_exception() {
            Mockito.when(repo.find(Mockito.anyInt())).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> service.findReviews(1));
        }

        @Test
        @DisplayName("When there are no reviews, should return empty list")
        void findReviewsOfBook_emptyList() {
            // given
            var book = TestHelper.getBookMock();
            Mockito.when(repo.find(Mockito.anyInt())).thenReturn(Optional.of(book));
            Mockito.when(repo.findReviewsOfBook(Mockito.anyInt())).thenReturn(new ArrayList<>());
            // when
            List<String> result = service.findReviews(book.getId());
            // then
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("When there are registered reviews, should return list")
        void findReviewsOfBook_result() {
            // given
            var book = TestHelper.getBookMock();
            var reviews = TestHelper.getReviewsMock();
            Mockito.when(repo.find(Mockito.anyInt())).thenReturn(Optional.of(book));
            Mockito.when(repo.findReviewsOfBook(Mockito.anyInt())).thenReturn(reviews);
            // when
            List<String> result = service.findReviews(book.getId());
            // then
            assertFalse(result.isEmpty());
            assertEquals(reviews.size(), result.size());
        }

    }

    @Nested
    class AddReviewTests {

        @Test
        @DisplayName("When book does not exist, should throw exception")
        void addReview_exception() {
            Mockito.when(repo.find(Mockito.anyInt())).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> service.addReview(1, "Review"));
        }

        @Test
        @DisplayName("When valid book and review should return result")
        void addReview_result() {
            // given
            var book = TestHelper.getBookMock();
            var review = TestHelper.getReviewMock();
            Mockito.when(repo.find(Mockito.anyInt())).thenReturn(Optional.of(book));
            Mockito.when(repo.addReview(Mockito.anyInt(), Mockito.anyString())).thenReturn(review);
            // when
            String result = service.addReview(book.getId(), review);
            // then
            assertEquals(review, result);
        }

    }

}