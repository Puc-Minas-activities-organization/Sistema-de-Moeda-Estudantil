package com.puc.moeda.repositories;

import com.puc.moeda.models.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {
  Professor findByEmail(String email);

  Professor findByCpf(String cpf);

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
