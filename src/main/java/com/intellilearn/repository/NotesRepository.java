package com.intellilearn.repository;

import com.intellilearn.entity.Notes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotesRepository extends JpaRepository<Notes, Long> {

    Optional<Notes> findBySubjectId(Long subjectId);

    boolean existsBySubjectId(Long subjectId);

}