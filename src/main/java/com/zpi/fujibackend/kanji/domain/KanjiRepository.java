package com.zpi.fujibackend.kanji.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface KanjiRepository extends JpaRepository<Kanji, Long> {

    List<Kanji> findByLevel(int level);

    Optional<Kanji> getByUuid(UUID uuid);

}
