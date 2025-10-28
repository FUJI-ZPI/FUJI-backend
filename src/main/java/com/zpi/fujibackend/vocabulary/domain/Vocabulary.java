package com.zpi.fujibackend.vocabulary.domain;


import com.zpi.fujibackend.common.entity.AbstractUuidEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;


@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "vocabulary", schema = "wanikani")
class Vocabulary extends AbstractUuidEntity {

    @Column(name = "level", nullable = false)
    private int level;

    @Column(name = "characters")
    private String characters;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "document", columnDefinition = "jsonb")
    private String document;

    @Column(name = "unicode_characters")
    private List<String> unicodeCharacters;


}
