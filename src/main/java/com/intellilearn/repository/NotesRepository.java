package com.intellilearn.repository;

import com.intellilearn.entity.Notes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotesRepository extends JpaRepository<Notes, Long> {

    List<Notes> findBySubjectId(Long subjectId);

    boolean existsBySubjectId(Long subjectId);

    void deleteBySubjectId(Long subjectId);

}
