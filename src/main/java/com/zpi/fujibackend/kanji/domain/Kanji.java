package com.zpi.fujibackend.kanji.domain;


import com.zpi.fujibackend.common.entity.AbstractUuidEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "kanji", schema = "wanikani")
public class Kanji extends AbstractUuidEntity {

    @Column(name = "level", nullable = false)
    private Integer level;

    @Column(name = "character")
    private String character;

    @Column(name = "unicode_character")
    private String unicodeCharacter;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "document", columnDefinition = "jsonb")
    private String document;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "drawing_data", columnDefinition = "jsonb")
    private String drawingData;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "svg_data", columnDefinition = "jsonb")
    private String svgData;

    @Column(name = "drawing_data_count")
    private Integer drawingDataCount;
}
