package com.epam.gym.entity;

import com.epam.gym.enums.UserRoleEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table()
@Entity
public class UserRole extends BaseEntity {
    @Column(name = "user_id")
    private Long userId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    private User user;
    @Enumerated(EnumType.STRING)
    private UserRoleEnum role;
}