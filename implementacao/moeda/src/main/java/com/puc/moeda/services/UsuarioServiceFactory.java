package com.puc.moeda.services;

import com.puc.moeda.models.Role;
import com.puc.moeda.services.interfaces.AutoCadastravel;
import com.puc.moeda.services.interfaces.UsuarioBasico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceFactory {

    @Autowired
    private AlunoService alunoService;

    @Autowired
    private EmpresaParceiraService empresaParceiraService;

    @Autowired
    private ProfessorService professorService;

    public UsuarioBasico<?, ?, ?, ?> getUsuarioService(Role role) {
        return switch (role) {
            case ALUNO -> alunoService;
            case EMPRESA_PARCEIRA -> empresaParceiraService;
            case PROFESSOR -> professorService;
            case ADMIN -> throw new UnsupportedOperationException("ADMIN não possui serviço de usuário básico");
        };
    }

    public AutoCadastravel<?, ?, ?> getAutoCadastravelService(Role role) {
        if (!role.podeSeAutoCadastrar()) {
            throw new UnsupportedOperationException(
                "Usuários do tipo " + role + " não podem se auto-cadastrar");
        }

        return switch (role) {
            case ALUNO -> alunoService;
            case EMPRESA_PARCEIRA -> empresaParceiraService;
            case ADMIN, PROFESSOR -> throw new UnsupportedOperationException(
                "Auto-cadastro não suportado para: " + role);
        };
    }

    public boolean podeSeAutoCadastrar(Role role) {
        return role.podeSeAutoCadastrar();
    }
}