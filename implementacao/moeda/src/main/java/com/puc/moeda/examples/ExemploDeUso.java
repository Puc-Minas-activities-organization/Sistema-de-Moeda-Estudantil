package com.puc.moeda.examples;

import com.puc.moeda.dto.AlunoRequest;
import com.puc.moeda.dto.EmpresaParceiraRequest;
import com.puc.moeda.dto.ProfessorRequest;
import com.puc.moeda.models.Role;
import com.puc.moeda.services.UsuarioServiceFactory;
import com.puc.moeda.services.interfaces.AutoCadastravel;
import com.puc.moeda.services.interfaces.UsuarioBasico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * EXEMPLOS DE USO DO SISTEMA DE CADASTRO
 * 
 * Este arquivo mostra como usar o sistema na prática.
 */
@Component
public class ExemploDeUso {

    @Autowired
    private UsuarioServiceFactory factory;

    // ==========================================
    // EXEMPLO 1: Cadastrar um Aluno ✅
    // ==========================================
    public void exemploAlunoCadastro() {
        // 1. Criar request com dados do aluno
        AlunoRequest request = new AlunoRequest();
        request.setEmail("aluno@exemplo.com");
        request.setSenha("senha123");
        request.setCpf("12345678900");
        request.setNome("João Silva");
        request.setRg("MG1234567");
        request.setInstituicao("PUC Minas");
        request.setCurso("Engenharia de Software");
        request.setEndereco("Rua Exemplo, 123");

        // 2. Verificar se pode se auto-cadastrar
        if (factory.podeSeAutoCadastrar(Role.ALUNO)) {
            // 3. Obter service via factory
            @SuppressWarnings("unchecked")
            AutoCadastravel<?, AlunoRequest, ?> service = 
                (AutoCadastravel<?, AlunoRequest, ?>) factory.getAutoCadastravelService(Role.ALUNO);
            
            // 4. Cadastrar
            Object aluno = service.cadastrar(request);
            System.out.println("Aluno cadastrado: " + aluno);
        }
    }

    // ==========================================
    // EXEMPLO 2: Cadastrar uma Empresa ✅
    // ==========================================
    public void exemploEmpresaCadastro() {
        EmpresaParceiraRequest request = new EmpresaParceiraRequest();
        request.setEmail("empresa@exemplo.com");
        request.setSenha("senha456");
        request.setCnpj("12345678000190");
        request.setNome("Tech Company LTDA");
        request.setEndereco("Av. Empresarial, 456");

        if (factory.podeSeAutoCadastrar(Role.EMPRESA_PARCEIRA)) {
            @SuppressWarnings("unchecked")
            AutoCadastravel<?, EmpresaParceiraRequest, ?> service = 
                (AutoCadastravel<?, EmpresaParceiraRequest, ?>) factory.getAutoCadastravelService(Role.EMPRESA_PARCEIRA);
            
            Object empresa = service.cadastrar(request);
            System.out.println("Empresa cadastrada: " + empresa);
        }
    }

    // ==========================================
    // EXEMPLO 3: Tentar cadastrar Professor ❌
    // ==========================================
    public void exemploProfessorCadastroFalha() {
        ProfessorRequest request = new ProfessorRequest();
        request.setEmail("professor@exemplo.com");
        request.setSenha("senha789");
        request.setCpf("98765432100");
        request.setNome("Maria Santos");
        request.setDepartamento("Computação");
        request.setInstituicao("PUC Minas");

        // Tentando se auto-cadastrar...
        if (factory.podeSeAutoCadastrar(Role.PROFESSOR)) {
            System.out.println("Professor pode se cadastrar");
        } else {
            System.out.println("❌ Professor NÃO pode se auto-cadastrar!");
            System.out.println("Apenas administradores podem cadastrar professores.");
        }
    }

    // ==========================================
    // EXEMPLO 4: Consultar Perfil (qualquer usuário) ✅
    // ==========================================
    public void exemploConsultarPerfil() {
        String email = "aluno@exemplo.com";
        Role tipo = Role.ALUNO;

        // Todos os usuários podem consultar perfil
        UsuarioBasico<?, ?, ?, ?> service = factory.getUsuarioService(tipo);
        Object perfil = service.consultarPerfil(email);
        System.out.println("Perfil: " + perfil);
    }

    // ==========================================
    // EXEMPLO 5: Alterar Dados (qualquer usuário) ✅
    // ==========================================
    public void exemploAlterarDados() {
        String email = "aluno@exemplo.com";
        
        AlunoRequest novosDados = new AlunoRequest();
        novosDados.setNome("João Silva Junior");
        novosDados.setCurso("Ciência da Computação");
        novosDados.setEndereco("Rua Nova, 789");

        @SuppressWarnings("unchecked")
        UsuarioBasico<?, AlunoRequest, ?, ?> service = 
            (UsuarioBasico<?, AlunoRequest, ?, ?>) factory.getUsuarioService(Role.ALUNO);
        
        Object atualizado = service.alterarDados(email, novosDados);
        System.out.println("Dados atualizados: " + atualizado);
    }

    // ==========================================
    // EXEMPLO 6: Validar Credenciais (Login) ✅
    // ==========================================
    public void exemploLogin() {
        String email = "aluno@exemplo.com";
        String senha = "senha123";

        UsuarioBasico<?, ?, ?, ?> service = factory.getUsuarioService(Role.ALUNO);
        
        if (service.validarCredenciais(email, senha)) {
            System.out.println("✅ Login bem-sucedido!");
        } else {
            System.out.println("❌ Credenciais inválidas!");
        }
    }

    // ==========================================
    // EXEMPLO 7: Tratamento de Exceções
    // ==========================================
    public void exemploComTratamentoDeErros() {
        try {
            // Tentando obter service de auto-cadastro para professor
            AutoCadastravel<?, ?, ?> service = 
                factory.getAutoCadastravelService(Role.PROFESSOR);
            
            // Esta linha nunca será executada
            System.out.println("Service obtido: " + service);
            
        } catch (UnsupportedOperationException e) {
            System.out.println("❌ Erro esperado: " + e.getMessage());
            // Saída: "Usuários do tipo PROFESSOR não podem se auto-cadastrar"
        }
    }

    // ==========================================
    // EXEMPLO 8: Verificando Permissões
    // ==========================================
    public void exemploVerificarPermissoes() {
        System.out.println("=== Verificando Permissões ===");
        
        for (Role role : Role.values()) {
            boolean podeAutoCadastrar = factory.podeSeAutoCadastrar(role);
            System.out.println(role + " pode se auto-cadastrar? " + 
                (podeAutoCadastrar ? "✅ SIM" : "❌ NÃO"));
        }
        
        /* Saída esperada:
         * PROFESSOR pode se auto-cadastrar? ❌ NÃO
         * ALUNO pode se auto-cadastrar? ✅ SIM
         * EMPRESA_PARCEIRA pode se auto-cadastrar? ✅ SIM
         */
    }
}
