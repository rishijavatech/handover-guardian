package com.koco.guardian.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AuthorityCreateRequest {
    @NotNull
    @NotEmpty
    private List<String> names;
}
