package com.tiagoamp.booksapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {

        Integer id;
        String title;
        String language;
        Integer yearOfPublication;
        String authors;

}
