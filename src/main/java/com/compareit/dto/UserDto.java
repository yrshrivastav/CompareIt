package com.compareit.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30)
    private String username;

    @NotBlank(message = "Password is required")
    @NotNull
    private String password;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "USER|ADMIN")
    private String role;


}


