package com.tiagoamp.booksapi.controller;

import com.tiagoamp.booksapi.dto.BookRequest;
import com.tiagoamp.booksapi.dto.BookResponse;
import com.tiagoamp.booksapi.dto.ReviewRequest;
import com.tiagoamp.booksapi.dto.ReviewResponse;
import com.tiagoamp.booksapi.service.BooksService;
import com.tiagoamp.booksapi.util.BookMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BooksController {

    private final BooksService service;
    private final BookMapper bookMapper;

    @GetMapping
    public ResponseEntity<List<BookResponse>> getBooks(
            @RequestParam(value = "size", required = false, defaultValue = "3") Integer size,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "sort", required = false, defaultValue = "title") String sortField,
            @RequestParam(value = "direction", required = false, defaultValue = "ASC") String sortDirection) {
        var books = service.findBooks(size, pageNumber, sortField, sortDirection);
        var booksResp = books.stream().map(bookMapper::toResponse)
                .map(b -> b.add( linkTo(methodOn(this.getClass()).getBook(b.getId())).withSelfRel() ))
                .toList();
        return ResponseEntity.ok(booksResp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBook(@PathVariable("id") Integer id) {
        var book = service.findBookById(id);
        var bookResp = bookMapper.toResponse(book)
                .add( linkTo(methodOn(this.getClass()).getReviews(id)).withRel("reviews") )
                .add( linkTo(methodOn(this.getClass()).getBooks(null, null, null, null))
                        .withRel("books") );
        return ResponseEntity.ok(bookResp);
    }

    @PostMapping
    public ResponseEntity<BookResponse> createBook(@RequestBody @Valid BookRequest request) {
        var book = bookMapper.toModel(request);
        book = service.createBook(book);
        var bookResp = bookMapper.toResponse(book);
        return ResponseEntity.created(URI.create(book.getId().toString())).body(bookResp);
    }

    @PutMapping("{id}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable("id") Integer id, @RequestBody @Valid BookRequest request) {
        var book = bookMapper.toModel(request);
        book.setId(id);
        book = service.updateBook(book);
        var bookResp = bookMapper.toResponse(book);
        return ResponseEntity.ok(bookResp);
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteBook(@PathVariable("id") Integer id) {
        service.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{bookId}/reviews")
    public ResponseEntity<List<ReviewResponse>> getReviews(@PathVariable("bookId") Integer bookId) {
        var reviews = service.findReviews(bookId);
        var reviewsResp = reviews.stream()
                .map(ReviewResponse::new)
                .map(r -> r.add( linkTo(methodOn(this.getClass()).getBook(bookId)).withRel("book") ))
                .toList();
        return ResponseEntity.ok(reviewsResp);
    }

    @PostMapping("{bookId}/reviews")
    public ResponseEntity<ReviewResponse> createReview(@PathVariable("bookId") Integer bookId, @RequestBody @Valid ReviewRequest request) {
        var review = request.getReview();
        review = service.addReview(bookId, review);
        var reviewResp = new ReviewResponse(review);
        return ResponseEntity.created(URI.create("/")).body(reviewResp);
    }

}
