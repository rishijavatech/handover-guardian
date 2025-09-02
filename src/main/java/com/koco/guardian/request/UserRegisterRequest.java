package com.koco.guardian.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class UserRegisterRequest {


    @NotNull
    private String email;

    @NotNull
    @ToString.Exclude
    private String password;

    @NotNull
    private String userRole;

    @NotNull
    private String userType;


}
