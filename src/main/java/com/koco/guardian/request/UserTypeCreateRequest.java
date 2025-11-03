package com.koco.guardian.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import static com.koco.guardian.constants.Constant.GENERIC_NAME_REGEX;

@Data
public class UserTypeCreateRequest {

    @NotBlank
    @Pattern(regexp = GENERIC_NAME_REGEX, message = "Invalid type name")
    private String name;
}
