package com.care4elders.userservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class User {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    public String phoneNumber;
    private Role role;

}
