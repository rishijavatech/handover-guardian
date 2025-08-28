package com.koco.guardian.request;
import lombok.Getter;
import lombok.ToString;

@Getter
public class UserLoginRequest {
    private String email;
    @ToString.Exclude
    private String password;
}
