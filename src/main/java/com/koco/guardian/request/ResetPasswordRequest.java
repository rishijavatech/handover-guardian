package com.koco.guardian.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static com.koco.guardian.constants.Constant.EMAIL_REGEX;
import static com.koco.guardian.constants.ValidationMessage.FIELD_MUST_NOT_BE_BLANK;

@Getter
@Setter
public class ResetPasswordRequest {

    @NotBlank(message = FIELD_MUST_NOT_BE_BLANK)
    private String username;

    @NotBlank
    @Pattern(regexp = EMAIL_REGEX, message = "Invalid email format")
    private String email;

    @NotBlank(message = FIELD_MUST_NOT_BE_BLANK)
    @ToString.Exclude
    private String oldPassword;

    @NotBlank(message = FIELD_MUST_NOT_BE_BLANK)
    @ToString.Exclude
    private String newPassword;
}
