package com.puc.moeda.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/hash")
public class HashController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/gerar/{senha}")
    public Map<String, Object> gerarHash(@PathVariable String senha) {
        String hash = passwordEncoder.encode(senha);
        boolean teste = passwordEncoder.matches(senha, hash);
        
        Map<String, Object> response = new HashMap<>();
        response.put("senhaTexto", senha);
        response.put("hashGerado", hash);
        response.put("testeValidacao", teste);
        
        System.out.println("=================================");
        System.out.println("Senha em texto: " + senha);
        System.out.println("Hash gerado: " + hash);
        System.out.println("Teste de validação: " + teste);
        System.out.println("=================================");
        System.out.println("\nSQL para atualizar no banco:");
        System.out.println("UPDATE usuario SET senha = '" + hash + "' WHERE email = 'joao.silva@puc.br';");
        System.out.println("UPDATE usuario SET senha = '" + hash + "' WHERE role = 'PROFESSOR';");
        System.out.println("UPDATE usuario SET senha = '" + hash + "' WHERE role = 'ALUNO';");
        System.out.println("UPDATE usuario SET senha = '" + hash + "' WHERE role = 'EMPRESA_PARCEIRA';");
        System.out.println("=================================");
        
        return response;
    }
}
