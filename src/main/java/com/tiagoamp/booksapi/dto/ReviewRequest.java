package com.tiagoamp.booksapi.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {

        @NotEmpty(message = "{required.field}")
        @Size(min = 1, max = 200, message = "{invalid.field}")
        String review;

}
