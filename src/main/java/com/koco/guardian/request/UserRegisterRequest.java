package com.koco.guardian.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static com.koco.guardian.constants.ValidationMessage.FIELD_MUST_NOT_BE_BLANK;

@Getter
@Setter
public class UserRegisterRequest {


    @NotNull
    private String email;

    @NotBlank(message = FIELD_MUST_NOT_BE_BLANK)
    @ToString.Exclude
    private String password;

    @NotNull
    private String userRole;

    @NotNull
    private String userType;

    @NotBlank
    private String userId;


}
