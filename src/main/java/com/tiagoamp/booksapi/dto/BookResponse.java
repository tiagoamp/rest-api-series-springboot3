package com.tiagoamp.booksapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse extends RepresentationModel<BookResponse> {

        Integer id;
        String title;
        String language;
        Integer yearOfPublication;
        String authors;

}
