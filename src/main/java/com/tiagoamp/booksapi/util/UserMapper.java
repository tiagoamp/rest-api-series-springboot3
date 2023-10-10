package com.tiagoamp.booksapi.util;

import com.tiagoamp.booksapi.dto.AppUserRequest;
import com.tiagoamp.booksapi.dto.AppUserResponse;
import com.tiagoamp.booksapi.dto.BookRequest;
import com.tiagoamp.booksapi.dto.BookResponse;
import com.tiagoamp.booksapi.model.AppUser;
import com.tiagoamp.booksapi.model.Book;
import com.tiagoamp.booksapi.repository.BookEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Provides objects mapping,
 * acting as a abstraction layer of mapper provider
 */
@Component
@RequiredArgsConstructor
public class UserMapper {

    private final ModelMapper mapper;


    public AppUser toModel(AppUserRequest request) {
        return mapper.map(request, AppUser.class);
    }

    public AppUserRequest toRequest(AppUser user) {
        return mapper.map(user, AppUserRequest.class);
    }

    public AppUserResponse toResponse(AppUser user) {
        return mapper.map(user, AppUserResponse.class);
    }

}
