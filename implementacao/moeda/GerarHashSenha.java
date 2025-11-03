import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GerarHashSenha {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String senhaTextoPlano = "senha123";
        String hashGerado = encoder.encode(senhaTextoPlano);
        
        System.out.println("=================================");
        System.out.println("Senha em texto: " + senhaTextoPlano);
        System.out.println("Hash gerado: " + hashGerado);
        System.out.println("=================================");
        System.out.println("\nUPDATE para o banco:");
        System.out.println("UPDATE usuario SET senha = '" + hashGerado + "' WHERE email = 'joao.silva@puc.br';");
        System.out.println("UPDATE usuario SET senha = '" + hashGerado + "' WHERE email = 'maria.santos@puc.br';");
        System.out.println("UPDATE usuario SET senha = '" + hashGerado + "' WHERE email = 'pedro.oliveira@puc.br';");
        System.out.println("UPDATE usuario SET senha = '" + hashGerado + "' WHERE email = 'carlos.almeida@sga.pucminas.br';");
        System.out.println("UPDATE usuario SET senha = '" + hashGerado + "' WHERE email = 'ana.costa@sga.pucminas.br';");
        System.out.println("UPDATE usuario SET senha = '" + hashGerado + "' WHERE email = 'bruno.ferreira@sga.pucminas.br';");
        System.out.println("UPDATE usuario SET senha = '" + hashGerado + "' WHERE role = 'EMPRESA_PARCEIRA';");
        System.out.println("=================================");
        
        // Verificar se o hash funciona
        boolean teste = encoder.matches(senhaTextoPlano, hashGerado);
        System.out.println("\nTeste de validação: " + (teste ? "✅ SUCESSO!" : "❌ FALHOU!"));
    }
}
