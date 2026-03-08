package com.koco.guardian.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AuthorityResponse {
    private List<String> authorities;
    private String message;
}
