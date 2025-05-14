package com.care4elders.paramedicalcare.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    public String phoneNumber;
    private String role;
    private String specialization;

    public UserDTO(String unknown, String user, String s) {
    }
}