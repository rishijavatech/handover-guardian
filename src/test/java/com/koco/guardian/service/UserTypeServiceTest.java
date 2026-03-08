package com.koco.guardian.service;

import com.koco.guardian.entity.UserType;
import com.koco.guardian.repository.UserTypeRepository;
import com.koco.guardian.request.UserTypeCreateRequest;
import com.koco.guardian.response.UserTypeResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserTypeServiceTest {

    @Mock
    private UserTypeRepository userTypeRepository;

    @InjectMocks
    private UserTypeService userTypeService;

    public UserTypeServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUserType_success() {
        UserTypeCreateRequest req = new UserTypeCreateRequest();
        req.setName("Admin");
        when(userTypeRepository.existsByName("Admin")).thenReturn(false);

        UserTypeResponse resp = userTypeService.createUserType(req);
        assertEquals("UserType created successfully", resp.getMessage());
        assertEquals("Admin", resp.getUserType());
        verify(userTypeRepository).save(any(UserType.class));
    }

    @Test
    void createUserType_alreadyExists() {
        UserTypeCreateRequest req = new UserTypeCreateRequest();
        req.setName("Admin");
        when(userTypeRepository.existsByName("Admin")).thenReturn(true);

        UserTypeResponse resp = userTypeService.createUserType(req);
        assertEquals("UserType already exists", resp.getMessage());
        assertEquals("Admin", resp.getUserType());
        verify(userTypeRepository, never()).save(any(UserType.class));
    }

    @Test
    void getAllUserTypes_returnsList() {
        when(userTypeRepository.findAll()).thenReturn(Collections.emptyList());
        List<UserType> result = userTypeService.getAllUserTypes();
        assertEquals(0, result.size());
    }

    @Test
    void getUserTypeById_found() {
        UserType type = new UserType();
        type.setName("Admin");
        when(userTypeRepository.findById(1L)).thenReturn(Optional.of(type));
        Optional<UserType> result = userTypeService.getUserTypeById(1L);
        assertEquals("Admin", result.get().getName());
    }

    @Test
    void getUserTypeById_notFound() {
        when(userTypeRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<UserType> result = userTypeService.getUserTypeById(1L);
        assertEquals(false, result.isPresent());
    }

    @Test
    void updateUserType_success() {
        UserType type = new UserType();
        type.setName("Old");
        when(userTypeRepository.findById(1L)).thenReturn(Optional.of(type));
        when(userTypeRepository.save(any(UserType.class))).thenReturn(type);

        UserTypeCreateRequest req = new UserTypeCreateRequest();
        req.setName("New");
        UserTypeResponse resp = userTypeService.updateUserType(1L, req);
        assertEquals("UserType updated successfully", resp.getMessage());
        assertEquals("New", resp.getUserType());

    }

    @Test
    void updateUserType_notFound() {
        when(userTypeRepository.findById(1L)).thenReturn(Optional.empty());
        UserTypeCreateRequest req = new UserTypeCreateRequest();
        req.setName("Admin"); // Use "Admin" here if that's what you expect
        UserTypeResponse resp = userTypeService.updateUserType(1L, req);
        assertEquals("UserType not found", resp.getMessage());
        assertEquals("Admin", resp.getUserType()); // Correct expectation
    }


    @Test
    void deleteUserType_success() {
        UserType type = new UserType();
        type.setName("Admin");
        when(userTypeRepository.findById(1L)).thenReturn(Optional.of(type));

        UserTypeResponse resp = userTypeService.deleteUserType(1L);
        assertEquals("UserType deleted successfully", resp.getMessage());
        assertEquals("Admin", resp.getUserType());
        verify(userTypeRepository).delete(type);
    }

    @Test
    void deleteUserType_notFound() {
        when(userTypeRepository.findById(1L)).thenReturn(Optional.empty());
        UserTypeResponse resp = userTypeService.deleteUserType(1L);
        assertEquals("UserType not found", resp.getMessage());
        assertEquals(null, resp.getUserType());
    }
}
