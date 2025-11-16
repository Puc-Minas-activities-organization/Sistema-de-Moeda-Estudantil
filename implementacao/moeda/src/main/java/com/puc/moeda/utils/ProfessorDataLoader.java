package com.puc.moeda.utils;

import com.puc.moeda.models.Professor;
import com.puc.moeda.repositories.ProfessorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class ProfessorDataLoader {
  @Bean
  CommandLineRunner initProfessores(ProfessorRepository professorRepository) {
    return args -> {
      if (professorRepository.count() == 0) {
        BCryptPasswordEncoder encoder =
            new BCryptPasswordEncoder();

        Professor p1 = new Professor("12345678900", "João Silva", "Matemática", "PUC Minas", 1000);
        p1.setEmail("professor@gmail.com");
        p1.setSenha(encoder.encode("123"));
        p1.setRole(com.puc.moeda.models.Role.PROFESSOR);
        professorRepository.save(p1);

        Professor p2 = new Professor("98765432100", "Maria Souza", "Física", "PUC Minas", 1000);
        p2.setEmail("maria.souza@pucminas.br");
        p2.setSenha(encoder.encode("senha123"));
        p2.setRole(com.puc.moeda.models.Role.PROFESSOR);
        professorRepository.save(p2);

        Professor p3 = new Professor("11122233344", "Carlos Pereira", "Química", "PUC Minas", 1000);
        p3.setEmail("carlos.pereira@pucminas.br");
        p3.setSenha(encoder.encode("senha123"));
        p3.setRole(com.puc.moeda.models.Role.PROFESSOR);
        professorRepository.save(p3);
      }
    };
  }
}
