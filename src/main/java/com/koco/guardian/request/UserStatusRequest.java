package com.koco.guardian.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserStatusRequest {

    @NotNull
    private String userId;
    @NotNull
    private boolean isActive;
}
