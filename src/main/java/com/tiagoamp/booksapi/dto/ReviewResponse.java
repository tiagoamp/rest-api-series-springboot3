package com.tiagoamp.booksapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse extends RepresentationModel<ReviewResponse> {

    String review;

}
