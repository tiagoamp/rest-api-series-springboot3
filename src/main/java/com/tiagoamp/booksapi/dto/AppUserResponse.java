package com.tiagoamp.booksapi.dto;

import lombok.Data;

@Data
public class AppUserResponse {

    private Integer id;

    private String name;

    private String email;

    private String Role;

}
