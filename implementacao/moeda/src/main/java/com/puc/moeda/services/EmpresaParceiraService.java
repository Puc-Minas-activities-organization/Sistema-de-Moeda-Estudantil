package com.puc.moeda.services;

import com.puc.moeda.dto.EmpresaParceiraRequest;
import com.puc.moeda.models.EmpresaParceira;
import com.puc.moeda.models.Role;
import com.puc.moeda.repositories.EmpresaParceiraRepository;
import com.puc.moeda.services.interfaces.AutoCadastravel;
import com.puc.moeda.services.interfaces.UsuarioBasico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class EmpresaParceiraService implements 
    AutoCadastravel<EmpresaParceira, EmpresaParceiraRequest, EmpresaParceira>,
    UsuarioBasico<EmpresaParceira, EmpresaParceiraRequest, EmpresaParceira, EmpresaParceira> {

    @Autowired
    private EmpresaParceiraRepository empresaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public EmpresaParceira cadastrar(EmpresaParceiraRequest request) {
        if (!validarDadosCadastro(request)) {
            throw new IllegalArgumentException("Dados de cadastro inválidos");
        }

        EmpresaParceira empresa = new EmpresaParceira();
        empresa.setEmail(request.getEmail());
        empresa.setSenha(passwordEncoder.encode(request.getSenha()));
        empresa.setCnpj(request.getCnpj());
        empresa.setNome(request.getNome());
        empresa.setRole(Role.EMPRESA_PARCEIRA);

        return empresaRepository.save(empresa);
    }

    @Override
    public boolean validarDadosCadastro(EmpresaParceiraRequest request) {
        // Validar se email já existe
        if (empresaRepository.findByEmail(request.getEmail()).isPresent()) {
            return false;
        }
        
        // Validar se CNPJ já existe
        if (empresaRepository.findByCnpj(request.getCnpj()).isPresent()) {
            return false;
        }
        
        // Outras validações...
        return request.getEmail() != null && 
               request.getSenha() != null && 
               request.getCnpj() != null;
    }

    @Override
    public EmpresaParceira alterarDados(String email, EmpresaParceiraRequest request) {
        EmpresaParceira empresa = empresaRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        
        // Atualizar campos permitidos
        if (request.getNome() != null) {
            empresa.setNome(request.getNome());
        }
        
        return empresaRepository.save(empresa);
    }

    @Override
    public EmpresaParceira consultarPerfil(String email) {
        return empresaRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
    }

    @Override
    public boolean validarCredenciais(String email, String senha) {
        return empresaRepository.findByEmail(email)
            .map(empresa -> passwordEncoder.matches(senha, empresa.getSenha()))
            .orElse(false);
    }
}