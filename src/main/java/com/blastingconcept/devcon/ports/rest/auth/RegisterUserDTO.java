package com.blastingconcept.devcon.ports.rest.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserDTO {

    @NotNull(message = "Name is required")
    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @NotNull(message = "Email is required")
    @Email( message = "Please include a valid email")
    private String email;

    @NotNull
    @Size(min = 6, message = "Please enter a password with 6 or more characters")
    private String password;
}
