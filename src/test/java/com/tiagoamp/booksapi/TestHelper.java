package com.tiagoamp.booksapi;

import com.tiagoamp.booksapi.dto.BookRequest;
import com.tiagoamp.booksapi.model.Book;
import com.tiagoamp.booksapi.repository.BookEntity;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

public class TestHelper {

    public static List<Book> getBooksMock() {
        var books = List.of(
                new Book(1, "title 1", "lang 1", 2001, "author 1"),
                new Book(2, "title 2", "lang 2", 2002, "author 2"),
                new Book(3, "title 3", "lang 3", 2003, "author 3"));
        return new ArrayList<>(books); // return a copy
    }

    public static Book getBookMock() {
        return getBooksMock().stream().findAny().get();
    }

    public static List<BookEntity> getBooksEntityWithNoIdMock() {
        var entities = getBooksEntityMock();
        entities.forEach(e -> e.setId(null));
        return entities;
    }

    public static List<BookEntity> getBooksEntityMock() {
        var entities = getBooksMock().stream()
                .map(b -> new ModelMapper().map(b, BookEntity.class)).toList();
        return entities;
    }

    public static BookEntity getBookEntityMock() {
        return getBooksEntityMock().stream().findAny().get();
    }

    public static List<String> getReviewsMock() {
        var reviews = List.of("Review Text 01", "Review Text 02", "Review Text 03");
        return new ArrayList<>(reviews);
    }

    public static String getReviewMock() {
        return getReviewsMock().stream().findAny().get();
    }

}
