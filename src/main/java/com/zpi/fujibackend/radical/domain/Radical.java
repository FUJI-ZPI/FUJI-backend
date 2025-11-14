package com.zpi.fujibackend.radical.domain;

import com.zpi.fujibackend.common.entity.AbstractUuidEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "radicals", schema = "wanikani")
class Radical extends AbstractUuidEntity {


    @Column(name = "level", nullable = false)
    private int level;

    @Column(name = "character")
    private String character;

    @Column(name = "character_unicode")
    private String characterUnicode;

    @Column(name = "slug", nullable = false)
    private String slug;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "document", columnDefinition = "jsonb")
    private String document;
}
