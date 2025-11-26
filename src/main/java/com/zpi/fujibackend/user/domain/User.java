package com.zpi.fujibackend.user.domain;

import com.zpi.fujibackend.common.entity.AbstractUuidEntity;
import com.zpi.fujibackend.progress.domain.Progress;
import jakarta.persistence.*;
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

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Progress progress;

    public User(final String email) {
        this.email = email;
        this.created = OffsetDateTime.now();
        this.progress = new Progress(this);
    }
}
