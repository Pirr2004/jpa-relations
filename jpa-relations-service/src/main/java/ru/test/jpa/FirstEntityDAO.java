package ru.test.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.test.entity.FirstEntity;

public interface FirstEntityDAO extends JpaRepository<FirstEntity, Long> {
}
