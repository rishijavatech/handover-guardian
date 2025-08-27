package com.koco.guardian.request;

import com.koco.guardian.enums.UserRole;
import com.koco.guardian.enums.UserType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class UserRegisterRequest {

    private String userName;

    @NotNull
    private String email;

    @NotNull
    @ToString.Exclude
    private String password;

    @NotNull
    private UserRole userRole;

    @NotNull
    private UserType userType;

    private String userId;

}
