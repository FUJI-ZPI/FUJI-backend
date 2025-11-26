package com.zpi.fujibackend.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
@FieldNameConstants
public abstract class AbstractUuidEntity extends AbstractEntity {

    @Column(name = "uuid", unique = true, nullable = false)
    private UUID uuid = UUID.randomUUID();
}