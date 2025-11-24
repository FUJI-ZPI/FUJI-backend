package com.zpi.fujibackend.user.domain;

import com.zpi.fujibackend.common.entity.AbstractUuidEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends AbstractUuidEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private OffsetDateTime created;

    @Column(nullable = false)
    private Integer level;

    private String fcmToken;

    public User(final String email) {
        this.email = email;
        created = OffsetDateTime.now();
        level = 1;
    }
}
