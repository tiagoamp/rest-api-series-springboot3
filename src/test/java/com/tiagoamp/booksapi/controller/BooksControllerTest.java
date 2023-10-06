package com.tiagoamp.booksapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiagoamp.booksapi.BooksApiApplication;
import com.tiagoamp.booksapi.TestHelper;
import com.tiagoamp.booksapi.dto.BookRequest;
import com.tiagoamp.booksapi.dto.ReviewRequest;
import com.tiagoamp.booksapi.exception.ResourceAlreadyExistsException;
import com.tiagoamp.booksapi.exception.ResourceNotFoundException;
import com.tiagoamp.booksapi.model.Book;
import com.tiagoamp.booksapi.service.BooksService;
import com.tiagoamp.booksapi.util.BookMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)  // disable security filters
class BooksControllerTest {

    @TestConfiguration  // Config to add beans to Spring Test Context
    static class TestConfig {
        @Bean
        public BookMapper getBookMapper() {
            return new BookMapper(new BooksApiApplication().getModelMapper());
        }
    }

    @MockBean
    private BooksService booksService;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BooksController controller;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper jsonMapper = new ObjectMapper();


    @Test
    @DisplayName("When Get All Books request and there are no results Should return empty list")
    public void whenGetAllRequest_emptyListResponse() throws Exception {
        Mockito.when(booksService.findBooks(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(new ArrayList<>());
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("When Get All Books request and there are registered books Should return list")
    public void whenGetAllRequest_resultListResponse() throws Exception {
        var books = TestHelper.getBooksMock();
        Mockito.when(booksService.findBooks(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(books);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", is(not(emptyArray()))))
                .andExpect(jsonPath("$", hasSize(books.size())));
    }


    @Test
    @DisplayName("When Get Book by non-existing id Should return error")
    public void whenGetByNonExistingIdRequest_resultError() throws Exception {
        Integer reqId = 1;
        Mockito.when(booksService.findBookById(Mockito.anyInt()))
                .thenThrow(new ResourceNotFoundException(Book.class.getSimpleName(), reqId));
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/books/{id}", reqId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.details").doesNotExist())
                .andExpect(jsonPath("$.title", is( ResourceNotFoundException.class.getSimpleName() )))
                .andExpect(jsonPath("$.message", containsString( Book.class.getSimpleName())) )
                .andExpect(jsonPath("$.message", containsString( reqId.toString())) );
    }

    @Test
    @DisplayName("When Get Book by id request Should result book response")
    public void whenGetByIdRequest_resultResponse() throws Exception {
        var book = TestHelper.getBookMock();
        Mockito.when(booksService.findBookById(Mockito.anyInt())).thenReturn(book);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/books/{id}", book.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id", is( book.getId() )))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$._links").exists());  // HATEOAS
    }


    @Test
    @DisplayName("When Post with invalid values request Should result validation error")
    public void whenPostInvalidValuesRequest_resultError() throws Exception {
        var invalidReq = new BookRequest();
        String json = jsonMapper.writeValueAsString(invalidReq);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/books")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("ValidationException")))
                .andExpect(jsonPath("$.details").exists())
                .andExpect(jsonPath("$.details.title").exists())
                .andExpect(jsonPath("$.details.language").exists());
    }

    @Test
    @DisplayName("When Post request of a existing entity Should result error")
    public void whenPostExistingRequest_resultError() throws Exception {
        var book = TestHelper.getBookMock();
        var req = bookMapper.toRequest(book);
        String json = jsonMapper.writeValueAsString(req);
        Mockito.when(booksService.createBook(Mockito.any(Book.class)))
                .thenThrow(new ResourceAlreadyExistsException(Book.class.getSimpleName(), book.getId()));
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/books")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.details").doesNotExist())
                .andExpect(jsonPath("$.title", is( ResourceAlreadyExistsException.class.getSimpleName() )))
                .andExpect(jsonPath("$.message", containsString( Book.class.getSimpleName())) )
                .andExpect(jsonPath("$.message", containsString( book.getId().toString())) );
    }

    @Test
    @DisplayName("When Post with valid request Should result book response")
    public void whenPostValidRequest_resultResponse() throws Exception {
        var book = TestHelper.getBookMock();
        var req = bookMapper.toRequest(book);
        String json = jsonMapper.writeValueAsString(req);
        Mockito.when(booksService.createBook(Mockito.any(Book.class))).thenReturn(book);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/books")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id", is( book.getId() )))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.language").exists())
                .andExpect(jsonPath("$.yearOfPublication").exists())
                .andExpect(jsonPath("$.authors").exists());
    }


    @Test
    @DisplayName("When Put with invalid values request Should result validation error")
    public void whenPutInvalidValuesRequest_resultError() throws Exception {
        var invalidReq = new BookRequest();
        String json = jsonMapper.writeValueAsString(invalidReq);
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/books/{id}",1)
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("ValidationException")))
                .andExpect(jsonPath("$.details").exists())
                .andExpect(jsonPath("$.details.title").exists())
                .andExpect(jsonPath("$.details.language").exists());
    }

    @Test
    @DisplayName("When Put request of a non-existing id Should result error")
    public void whenPutNonExistingIdRequest_resultError() throws Exception {
        var book = TestHelper.getBookMock();
        var req = bookMapper.toRequest(book);
        String json = jsonMapper.writeValueAsString(req);
        Mockito.when(booksService.updateBook(Mockito.any(Book.class)))
                .thenThrow(new ResourceNotFoundException(Book.class.getSimpleName(), book.getId()));
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/books/{id}", book.getId())
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.details").doesNotExist())
                .andExpect(jsonPath("$.title", is( ResourceNotFoundException.class.getSimpleName() )))
                .andExpect(jsonPath("$.message", containsString( Book.class.getSimpleName())) )
                .andExpect(jsonPath("$.message", containsString( book.getId().toString())) );
    }

    @Test
    @DisplayName("When Put with valid request Should result book response")
    public void whenPutValidRequest_resultResponse() throws Exception {
        var book = TestHelper.getBookMock();
        var req = bookMapper.toRequest(book);
        String json = jsonMapper.writeValueAsString(req);
        Mockito.when(booksService.updateBook(Mockito.any(Book.class))).thenReturn(book);
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/books/{id}", book.getId())
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id", is( book.getId() )))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.language").exists())
                .andExpect(jsonPath("$.yearOfPublication").exists())
                .andExpect(jsonPath("$.authors").exists());
    }


    @Test
    @DisplayName("When Delete request of a non-existing id Should result error")
    public void whenDeleteNonExistingIdRequest_resultError() throws Exception {
        Integer reqId = 1;
        Mockito.doThrow(new ResourceNotFoundException(Book.class.getSimpleName(), reqId))
                .when(booksService).deleteBook(Mockito.anyInt());
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/books/{id}", reqId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.details").doesNotExist())
                .andExpect(jsonPath("$.title", is( ResourceNotFoundException.class.getSimpleName() )))
                .andExpect(jsonPath("$.message", containsString( Book.class.getSimpleName())) )
                .andExpect(jsonPath("$.message", containsString( reqId.toString())) );
    }

    @Test
    @DisplayName("When Delete with valid request Should result response")
    public void whenDeleteRequest_resultResponse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/books/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }


    @Test
    @DisplayName("When Get All Reviews of a book request and there are no results Should return empty list")
    public void whenGetAllReviewsRequest_emptyListResponse() throws Exception {
        Mockito.when(booksService.findReviews(Mockito.anyInt())).thenReturn(new ArrayList<>());
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/books/{bookId}/reviews", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("When Get All Reviews of a book request and there are registered books Should return list")
    public void whenGetAllReviewsRequest_resultListResponse() throws Exception {
        var reviews = TestHelper.getReviewsMock();
        Mockito.when(booksService.findReviews(Mockito.anyInt())).thenReturn(reviews);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/books/{bookId}/reviews", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", is(not(emptyArray()))))
                .andExpect(jsonPath("$", hasSize(reviews.size())));
    }


    @Test
    @DisplayName("When Post Review with invalid Review values request Should result validation error")
    public void whenPostReviewInvalidValuesRequest_resultError() throws Exception {
        var invalidReq = new ReviewRequest();
        String json = jsonMapper.writeValueAsString(invalidReq);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/books/{bookId}/reviews", 1)
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("ValidationException")))
                .andExpect(jsonPath("$.details").exists())
                .andExpect(jsonPath("$.details.review").exists());
    }

    @Test
    @DisplayName("When Post Review with valid request Should result book response")
    public void whenPostReviewValidRequest_resultResponse() throws Exception {
        var review = TestHelper.getReviewMock();
        String json = jsonMapper.writeValueAsString( new ReviewRequest(review) );
        Mockito.when(booksService.addReview(Mockito.anyInt(), Mockito.anyString())).thenReturn(review);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/books/{bookId}/reviews", 1)
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.review").exists());
    }

}