package com.puc.moeda.services;

import com.puc.moeda.dto.ProfessorRequest;
import com.puc.moeda.models.Professor;
import com.puc.moeda.repositories.ProfessorRepository;
import com.puc.moeda.services.interfaces.UsuarioBasico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service para Professor
 * IMPORTANTE: Professor NÃO implementa AutoCadastravel
 * Somente Admin pode cadastrar professores
 */
@Service
public class ProfessorService implements 
    UsuarioBasico<Professor, ProfessorRequest, Professor, Professor> {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Professor alterarDados(String email, ProfessorRequest request) {
        Professor professor = professorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
        
        // Atualizar campos permitidos
        if (request.getNome() != null) {
            professor.setNome(request.getNome());
        }
        if (request.getDepartamento() != null) {
            professor.setDepartamento(request.getDepartamento());
        }
        if (request.getInstituicao() != null) {
            professor.setInstituicao(request.getInstituicao());
        }
        
        return professorRepository.save(professor);
    }

    @Override
    public Professor consultarPerfil(String email) {
        return professorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
    }

    @Override
    public boolean validarCredenciais(String email, String senha) {
        return professorRepository.findByEmail(email)
            .map(professor -> passwordEncoder.matches(senha, professor.getSenha()))
            .orElse(false);
    }
}
