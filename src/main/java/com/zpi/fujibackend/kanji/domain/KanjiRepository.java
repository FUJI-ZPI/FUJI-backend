package com.zpi.fujibackend.kanji.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface KanjiRepository extends JpaRepository<Kanji, Long> {

    List<Kanji> findByLevel(int level);

}
