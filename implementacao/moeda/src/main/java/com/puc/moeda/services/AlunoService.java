package com.puc.moeda.services;

import com.puc.moeda.dto.AlunoRequest;
import com.puc.moeda.models.Aluno;
import com.puc.moeda.models.Role;
import com.puc.moeda.repositories.AlunoRepository;
import com.puc.moeda.services.interfaces.AutoCadastravel;
import com.puc.moeda.services.interfaces.UsuarioBasico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AlunoService implements 
    AutoCadastravel<Aluno, AlunoRequest, Aluno>,
    UsuarioBasico<Aluno, AlunoRequest, Aluno, Aluno> {

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Aluno cadastrar(AlunoRequest request) {
        if (!validarDadosCadastro(request)) {
            throw new IllegalArgumentException("Dados de cadastro inválidos");
        }

        Aluno aluno = new Aluno();
        aluno.setEmail(request.getEmail());
        aluno.setSenha(passwordEncoder.encode(request.getSenha()));
        aluno.setCpf(request.getCpf());
        aluno.setRg(request.getRg());
        aluno.setNome(request.getNome());
        aluno.setInstituicao(request.getInstituicao());
        aluno.setCurso(request.getCurso());
        aluno.setEndereco(request.getEndereco());
        aluno.setRole(Role.ALUNO);
        aluno.setSaldoMoedas(0.0);

        return alunoRepository.save(aluno);
    }

    @Override
    public boolean validarDadosCadastro(AlunoRequest request) {
        // Validar se email já existe
        if (alunoRepository.findByEmail(request.getEmail()).isPresent()) {
            return false;
        }
        
        // Validar se CPF já existe
        if (alunoRepository.findByCpf(request.getCpf()).isPresent()) {
            return false;
        }
        
        // Outras validações...
        return request.getEmail() != null && 
               request.getSenha() != null && 
               request.getCpf() != null;
    }

    @Override
    public Aluno alterarDados(String email, AlunoRequest request) {
        Aluno aluno = alunoRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
        
        // Atualizar campos permitidos
        if (request.getNome() != null) {
            aluno.setNome(request.getNome());
        }
        if (request.getInstituicao() != null) {
            aluno.setInstituicao(request.getInstituicao());
        }
        if (request.getCurso() != null) {
            aluno.setCurso(request.getCurso());
        }
        if (request.getEndereco() != null) {
            aluno.setEndereco(request.getEndereco());
        }
        
        return alunoRepository.save(aluno);
    }

    @Override
    public Aluno consultarPerfil(String email) {
        return alunoRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
    }

    @Override
    public boolean validarCredenciais(String email, String senha) {
        return alunoRepository.findByEmail(email)
            .map(aluno -> passwordEncoder.matches(senha, aluno.getSenha()))
            .orElse(false);
    }
}