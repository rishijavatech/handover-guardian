package com.koco.guardian.service;

import com.koco.guardian.entity.UserType;
import com.koco.guardian.repository.UserTypeRepository;
import com.koco.guardian.request.UserTypeCreateRequest;
import com.koco.guardian.response.UserTypeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserTypeService {

    private final UserTypeRepository userTypeRepository;

    public UserTypeResponse createUserType(UserTypeCreateRequest request) {
        if (userTypeRepository.existsByName(request.getName())) {
            return new UserTypeResponse(request.getName(), "UserType already exists");
        }
        UserType type = new UserType();
        type.setName(request.getName());
        userTypeRepository.save(type);
        return new UserTypeResponse(type.getName(), "UserType created successfully");
    }

    public List<UserType> getAllUserTypes() {
        return userTypeRepository.findAll();
    }

    public Optional<UserType> getUserTypeById(Long id) {
        return userTypeRepository.findById(id);
    }

    public UserTypeResponse updateUserType(Long id, UserTypeCreateRequest request) {
        return userTypeRepository.findById(id).map(type -> {
            type.setName(request.getName());
            userTypeRepository.save(type);
            return new UserTypeResponse(type.getName(), "UserType updated successfully");
        }).orElse(new UserTypeResponse(request.getName(), "UserType not found"));
    }

    public UserTypeResponse deleteUserType(Long id) {
        return userTypeRepository.findById(id).map(type -> {
            userTypeRepository.delete(type);
            return new UserTypeResponse(type.getName(), "UserType deleted successfully");
        }).orElse(new UserTypeResponse(null, "UserType not found"));
    }
}
