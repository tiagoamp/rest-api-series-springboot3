package com.tiagoamp.booksapi.controller;

import com.tiagoamp.booksapi.dto.RootEntryPointResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class RootEntryPointController {

    @GetMapping
    public ResponseEntity<RootEntryPointResponse> getRoot() {
        RootEntryPointResponse resp = new RootEntryPointResponse()
                .add( linkTo(methodOn(BooksController.class).getBooks(null, null, null, null))
                        .withRel("books") );
        return ResponseEntity.ok(resp);
    }

}
