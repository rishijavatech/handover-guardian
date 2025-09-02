package com.koco.guardian.request;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

@Getter
public class UserLoginRequest {
    @NotNull
    private String email;
    @ToString.Exclude
    @NotNull
    private String password;
}
