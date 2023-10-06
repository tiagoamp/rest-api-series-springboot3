package com.tiagoamp.booksapi.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookRequest {

        @NotEmpty(message = "{required.field}")
        @Size(min = 1, max = 200, message = "{invalid.field}")
        String title;

        @NotEmpty(message = "{required.field}")
        @Size(min = 1, max = 50, message = "{invalid.field}")
        String language;

        @PositiveOrZero(message = "{invalid.field}")
        Integer yearOfPublication;

        @Size(min = 1, max = 200, message = "{invalid.field}")
        String authors;

}
