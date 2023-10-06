package com.tiagoamp.booksapi.repository;

import com.tiagoamp.booksapi.BooksApiApplication;
import com.tiagoamp.booksapi.TestHelper;
import com.tiagoamp.booksapi.model.Book;
import com.tiagoamp.booksapi.util.BookMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookGatewayRepositoryTest {

    @Mock
    private BookRepository bookRepo;

    @Spy  // injects this real object
    private BookMapper mapper = new BookMapper(new BooksApiApplication().getModelMapper());

    @InjectMocks
    private BookGatewayRepository gatewayRepo;

    @Nested
    class FindAllTests {

        @Test
        @DisplayName("When no books registered, should return empty list")
        void findAll_empty() {
            // given
            Mockito.when(bookRepo.findAll(Mockito.any(PageRequest.class))).thenReturn(Page.empty());
            // when
            List<Book> result = gatewayRepo.findAll(10, 0, "title", "ASC");
            // then
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("When it is fetched records from database, should return list")
        void findAll() {
            // given
            var mocks = TestHelper.getBooksEntityMock();
            Mockito.when(bookRepo.findAll(Mockito.any(PageRequest.class))).thenReturn(new PageImpl<>(mocks));
            // when
            List<Book> result = gatewayRepo.findAll(10, 0, "title", "ASC");
            // then
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(mocks.size(), result.size());
        }

    }

    @Nested
    class FindTests {

        @Test
        @DisplayName("When id not found, should return empty result")
        void find_empty() {
            // given
            Mockito.when(bookRepo.findById(Mockito.anyInt())).thenReturn(Optional.empty());
            // when
            var result = gatewayRepo.find(1);
            // then
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("When found book by given id, should return it")
        void find() {
            // given
            var book = TestHelper.getBookEntityMock();
            Mockito.when(bookRepo.findById(Mockito.anyInt())).thenReturn(Optional.of(book));
            // when
            var result = gatewayRepo.find(book.getId());
            // then
            assertFalse(result.isEmpty());
            assertEquals(book.getId(), result.get().getId());
        }

    }

    @Nested
    class SaveTests {

        @Test
        @DisplayName("When book was saved, should return persisted instance")
        void save() {
            // given
            var entity = TestHelper.getBookEntityMock();
            entity.setId(1);
            Mockito.when(bookRepo.save(Mockito.any(BookEntity.class))).thenReturn(entity);
            // when
            var result = gatewayRepo.save(new Book());
            // then
            assertNotNull(result);
        }

    }

    @Nested
    class FindByTitleTests {

        @Test
        @DisplayName("When title not found, should return empty result")
        void findByTitle_empty() {
            var result = gatewayRepo.findBookByTitle("Not Existing Title");
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("When exists book with given title, should return book")
        void findByTitle() {
            // given
            var entity = TestHelper.getBookEntityMock();
            String title = entity.getTitle();
            Mockito.when(bookRepo.findByTitle(title)).thenReturn(Optional.of(entity));
            // when
            var result = gatewayRepo.findBookByTitle(title);
            // then
            assertTrue(result.isPresent());
            assertEquals(title, result.get().getTitle());
        }

    }

    @Nested
    class UpdateTests {

        @Test
        @DisplayName("When updated values, should return updated result")
        void update() {
            // given
            var bookBD = TestHelper.getBookEntityMock();
            var updatedBook = new Book();
            updatedBook.setId(bookBD.getId());
            // updating values
            updatedBook.setTitle("Updated Title");
            updatedBook.setLanguage("Updated Language");
            updatedBook.setYearOfPublication(2000);
            updatedBook.setAuthors("Updated Authors");
            var updatedEntity = mapper.toEntity(updatedBook);
            Mockito.when(bookRepo.findById(Mockito.anyInt())).thenReturn(Optional.of(bookBD));
            Mockito.when(bookRepo.save(Mockito.any(BookEntity.class))).thenReturn(updatedEntity);
            // when
            var result = gatewayRepo.update(updatedBook);
            // then
            assertEquals(bookBD.getId(), result.getId());
            assertEquals(updatedBook.getTitle(), result.getTitle());
            assertEquals(updatedBook.getLanguage(), result.getLanguage());
            assertEquals(updatedBook.getYearOfPublication(), result.getYearOfPublication());
            assertEquals(updatedBook.getAuthors(), result.getAuthors());
        }

    }

    @Nested
    class DeleteTests {

        @Test
        @DisplayName("When deleted book, should result no errors")
        void delete() {
            var book = TestHelper.getBookEntityMock();
            Integer id = book.getId();
            Mockito.when(bookRepo.findById(id)).thenReturn(Optional.of(book));
            assertDoesNotThrow(() -> gatewayRepo.delete(id));
        }

    }

    @Nested
    class FindReviewOfBookTests {

        @Test
        @DisplayName("When book has no reviews, should return empty list")
        void findReviewsOfBook_empty() {
            // given
            var book = TestHelper.getBookEntityMock();
            Mockito.when(bookRepo.findById(Mockito.anyInt())).thenReturn(Optional.of(book));
            // when
            var result = gatewayRepo.findReviewsOfBook(book.getId());
            // then
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("When book has reviews, should return list")
        void findReviewsOfBook() {
            // given
            var book = TestHelper.getBookEntityMock();
            var reviews = TestHelper.getReviewsMock();
            book.setReviews(reviews);
            Mockito.when(bookRepo.findById(Mockito.anyInt())).thenReturn(Optional.of(book));
            // when
            var result = gatewayRepo.findReviewsOfBook(book.getId());
            // then
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(reviews.size(), result.size());
        }

    }

    @Nested
    class AddReviewOfBookTests {

        @Test
        @DisplayName("When first review, should return result")
        void addReview_first() {
            // given
            var book = TestHelper.getBookEntityMock();
            var newReview = "New Review";
            Mockito.when(bookRepo.findById(Mockito.anyInt())).thenReturn(Optional.of(book));
            // when
            var result = gatewayRepo.addReview(book.getId(), newReview);
            // then
            assertEquals(newReview, result);
        }

        @Test
        @DisplayName("When new review Should return result")
        void addReview_newReview() {
            // given
            var book = TestHelper.getBookEntityMock();
            var reviews = TestHelper.getReviewsMock();
            book.setReviews(reviews);
            var newReview = "New Review";
            Mockito.when(bookRepo.findById(Mockito.anyInt())).thenReturn(Optional.of(book));
            // when
            var result = gatewayRepo.addReview(book.getId(), newReview);
            // then
            assertEquals(newReview, result);
        }

    }

}