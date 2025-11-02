package com.puc.moeda.repositories;

import com.puc.moeda.models.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {
  Optional<Professor> findByEmail(String email);

  Optional<Professor> findByCpf(String cpf);

  default Professor saveProfessor(Professor professor) {
    return save(professor);
  }

  default Professor findProfessorById(Long id) {
    return findById(id).orElse(null);
  }

  default void deleteProfessorById(Long id) {
    deleteById(id);
  }

  default java.util.List<Professor> findAllProfessores() {
    return findAll();
  }
}
