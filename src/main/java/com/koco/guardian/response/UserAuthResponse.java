package com.koco.guardian.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthResponse {
    private String userId;
    private String email;
    private String authToken;
    private String message;
}
