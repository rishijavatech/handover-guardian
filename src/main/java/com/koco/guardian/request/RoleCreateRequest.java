package com.koco.guardian.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import static com.koco.guardian.constants.Constant.ROLE_NAME_REGEX;

@Data
public class RoleCreateRequest {

    @NotBlank
    @Pattern(regexp = ROLE_NAME_REGEX, message = "Invalid role mame")
    private String role;
}
