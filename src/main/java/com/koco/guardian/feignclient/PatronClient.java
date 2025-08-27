package com.koco.guardian.feignclient;

import com.koco.guardian.request.UserRegisterRequest;
import com.koco.guardian.response.UserAuthResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "patron")
public interface PatronClient {

    @PostMapping("/api/v1/patron/users")
    UserAuthResponse createUser(@RequestBody UserRegisterRequest request);
}
