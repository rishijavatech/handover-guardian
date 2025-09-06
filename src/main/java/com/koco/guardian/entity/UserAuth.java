package com.koco.guardian.entity;

import com.koco.guardian.dto.ParentEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_auth")
@Getter
@Setter
public class UserAuth extends ParentEntity {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String email;
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private UserRole role;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_id")
    private UserType type;

    private boolean isActive = true;

    private boolean isDeleted = false;

}
