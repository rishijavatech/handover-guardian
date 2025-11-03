package com.koco.guardian.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.ToString;

import static com.koco.guardian.constants.Constant.EMAIL_REGEX;

@Getter
public class UserLoginRequest {
    @NotBlank
    @Pattern(regexp = EMAIL_REGEX, message = "Invalid email format")
    private String email;
    @ToString.Exclude
    @NotBlank
    private String password;
}
