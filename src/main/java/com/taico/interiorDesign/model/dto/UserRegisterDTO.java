package com.taico.interiorDesign.model.dto;

import com.taico.interiorDesign.error.annotation.UniqueEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterDTO {

    @NotBlank(message = "Моля, въведете име.")
    @Size(min = 2, max = 30)
    private String firstName;

    @NotBlank(message = "Моля, въведете фамилия.")
    @Size(min = 2, max = 30)
    private String lastName;

    @NotBlank(message = "Моля, въведете потребителско име.")
    @Size(min = 3, max = 30)
    private String username;

    @NotBlank(message = "Моля, въведете имейл.")
    @Email(message = "Невалиден имейл адрес.")
    @UniqueEmail(message = "Този имейл вече е регистриран.")
    private String email;

    @NotBlank(message = "Моля, въведете парола.")
    @Size(min = 6, max = 30)
    private String password;

    @NotBlank(message = "Моля, потвърдете паролата.")
    private String confirmPassword;
}
