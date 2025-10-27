package com.zpi.fujibackend.vocabulary.domain;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {

    List<Vocabulary> getVocabularyByLevel(int level);

    Optional<Vocabulary> findByUuid(UUID uuid);

}
