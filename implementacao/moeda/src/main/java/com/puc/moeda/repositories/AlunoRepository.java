package com.puc.moeda.repositories;

import com.puc.moeda.models.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {
  Optional<Aluno> findByEmail(String email);

  Optional<Aluno> findByCpf(String cpf);

  default Aluno saveAluno(Aluno aluno) {
    return save(aluno);
  }

  default Aluno findAlunoById(Long id) {
    return findById(id).orElse(null);
  }

  default void deleteAlunoById(Long id) {
    deleteById(id);
  }

  default java.util.List<Aluno> findAllAlunos() {
    return findAll();
  }
}
