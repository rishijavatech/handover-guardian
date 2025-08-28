package com.koco.guardian.request;

import com.koco.guardian.entity.UserRole;
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
    private String userRole;

    @NotNull
    private String userType;

    private String userId;

}
