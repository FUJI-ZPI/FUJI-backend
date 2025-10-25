package com.zpi.fujibackend.vocabulary.domain;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {

    List<Vocabulary> getVocabularyByLevel(int level);

}
