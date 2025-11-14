package com.zpi.fujibackend.radical.domain;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RadicalRepository extends JpaRepository<Radical, Long> {

    List<Radical> findByLevel(int level);

    Optional<Radical> getByUuid(UUID uuid);
}
