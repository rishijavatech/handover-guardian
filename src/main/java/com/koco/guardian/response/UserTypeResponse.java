package com.koco.guardian.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserTypeResponse {
    private String userType;
    private String message;
}
