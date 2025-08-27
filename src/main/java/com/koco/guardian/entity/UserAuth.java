package com.koco.guardian.entity;

import com.koco.guardian.enums.UserRole;
import com.koco.guardian.enums.UserType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user_auth")
@Getter
@Setter
public class UserAuth {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole role;
//    @Transient
//    @ToString.Exclude
//    private List<String> authorities;
    @CreationTimestamp
    private LocalDateTime createdOn;
}
