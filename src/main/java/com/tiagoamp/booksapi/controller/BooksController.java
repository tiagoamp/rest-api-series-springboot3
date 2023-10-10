package com.tiagoamp.booksapi.controller;

import com.tiagoamp.booksapi.dto.*;
import com.tiagoamp.booksapi.service.BooksService;
import com.tiagoamp.booksapi.util.BookMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Operation( summary = "Find books", description = "Get registered books" )
    @GetMapping
    @RolesAllowed( {"ADMIN","USER"} ) // needs to enable 'EnableGlobalMethodSecurity' at security class to work
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

    @Operation(summary = "Find book by id", description = "Find book by id",
            responses = {  @ApiResponse( responseCode = "404", description = "Book not found",
                    content = { @Content(schema = @Schema(implementation = ErrorResponse.class)) }) } )
    @GetMapping("/{id}")
    @RolesAllowed( {"ADMIN","USER"} )
    public ResponseEntity<BookResponse> getBook(@PathVariable("id") Integer id) {
        var book = service.findBookById(id);
        var bookResp = bookMapper.toResponse(book)
                .add( linkTo(methodOn(this.getClass()).getReviews(id)).withRel("reviews") )
                .add( linkTo(methodOn(this.getClass()).getBooks(null, null, null, null))
                        .withRel("books") );
        return ResponseEntity.ok(bookResp);
    }

    @Operation(summary = "Register new book", description = "Register new book",
            responses = {  @ApiResponse( responseCode = "400", description = "Invalid Request data",
                    content = { @Content(schema = @Schema(implementation = ErrorResponse.class)) })  } )
    @PostMapping
    @RolesAllowed("ADMIN")
    @ResponseStatus(HttpStatus.CREATED)  // This annotation helps Swagger to automatically generate documentation
    public ResponseEntity<BookResponse> createBook(@RequestBody @Valid BookRequest request) {
        var book = bookMapper.toModel(request);
        book = service.createBook(book);
        var bookResp = bookMapper.toResponse(book);
        return ResponseEntity.created(URI.create(book.getId().toString())).body(bookResp);
    }

    @Operation(summary = "Update book info", description = "Update book info",
            responses = {
                    @ApiResponse( responseCode = "400", description = "Invalid Request data",
                            content = { @Content(schema = @Schema(implementation = ErrorResponse.class)) } ),
                    @ApiResponse( responseCode = "404", description = "Book not found",
                            content = { @Content(schema = @Schema(implementation = ErrorResponse.class)) } )
            })
    @PutMapping("{id}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<BookResponse> updateBook(@PathVariable("id") Integer id, @RequestBody @Valid BookRequest request) {
        var book = bookMapper.toModel(request);
        book.setId(id);
        book = service.updateBook(book);
        var bookResp = bookMapper.toResponse(book);
        return ResponseEntity.ok(bookResp);
    }

    @Operation(
            summary = "Delete book by id", description = "Delete book by id",
            responses = {  @ApiResponse( responseCode = "404", description = "Book not found",
                    content = { @Content(schema = @Schema(implementation = ErrorResponse.class)) })  })
    @DeleteMapping("{id}")
    @RolesAllowed("ADMIN") // needs to enable 'EnableGlobalMethodSecurity' at security class to work
    @ResponseStatus(HttpStatus.NO_CONTENT)  // This annotation helps Swagger to automatically generate documentation
    public ResponseEntity deleteBook(@PathVariable("id") Integer id) {
        service.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @Operation( summary = "Find reviews", description = "Get review of a book" )
    @RolesAllowed( {"ADMIN","USER"} )
    @GetMapping("{bookId}/reviews")
    public ResponseEntity<List<ReviewResponse>> getReviews(@PathVariable("bookId") Integer bookId) {
        var reviews = service.findReviews(bookId);
        var reviewsResp = reviews.stream()
                .map(ReviewResponse::new)
                .map(r -> r.add( linkTo(methodOn(this.getClass()).getBook(bookId)).withRel("book") ))
                .toList();
        return ResponseEntity.ok(reviewsResp);
    }

    @Operation(
            summary = "Add new review", description = "Add new review to a book",
            responses = {
                    @ApiResponse( responseCode = "400", description = "Invalid Request data",
                            content = { @Content(schema = @Schema(implementation = ErrorResponse.class)) } ),
                    @ApiResponse( responseCode = "404", description = "Book not found",
                            content = { @Content(schema = @Schema(implementation = ErrorResponse.class)) } )
            })
    @PostMapping("{bookId}/reviews")
    @RolesAllowed( {"ADMIN","USER"} )
    @ResponseStatus(HttpStatus.CREATED)  // This annotation helps Swagger to automatically generate documentation
    public ResponseEntity<ReviewResponse> createReview(@PathVariable("bookId") Integer bookId, @RequestBody @Valid ReviewRequest request) {
        var review = request.getReview();
        review = service.addReview(bookId, review);
        var reviewResp = new ReviewResponse(review);
        return ResponseEntity.created(URI.create("/")).body(reviewResp);
    }

}
