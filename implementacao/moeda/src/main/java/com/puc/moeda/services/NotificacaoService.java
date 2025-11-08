package com.puc.moeda.services;

import com.puc.moeda.models.Aluno;
import com.puc.moeda.models.Notificacao;
import com.puc.moeda.models.ResgateBeneficio;
import com.puc.moeda.models.TransacaoMoeda;
import com.puc.moeda.repositories.NotificacaoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Serviço de notificação por email
 * TODO: Implementar integração real com serviço de email (SendGrid, AWS SES, etc)
 */
@Service
@Slf4j
public class NotificacaoService {
    
    @Autowired(required = false)
    private NotificacaoRepository notificacaoRepository;
    
    /**
     * Notifica aluno quando recebe moedas
     */
    public void notificarRecebimentoMoedas(TransacaoMoeda transacao) {
        Aluno aluno = transacao.getDestinatario();
        String emailDestinatario = aluno.getEmail();
        
        String assunto = String.format("Você recebeu %.2f moedas!", transacao.getValor());
        String corpo = String.format("""
            Olá %s,
            
            Você recebeu %.2f moedas do professor %s.
            
            Motivo: %s
            
            Seu saldo atual: %.2f moedas
            
            Acesse o sistema para ver mais detalhes!
            """,
            aluno.getNome(),
            transacao.getValor(),
            transacao.getRemetente().getNome(),
            transacao.getMensagem(),
            aluno.getSaldoMoedas()
        );
        
        enviarEmail(emailDestinatario, assunto, corpo);
        
        // Armazenar notificação no banco
        if (notificacaoRepository != null) {
            salvarNotificacao(aluno, "RECEBIMENTO_MOEDAS", assunto, corpo, String.valueOf(transacao.getId()));
        }
    }
    
    /**
     * Notifica aluno quando resgata um benefício (com código)
     */
    public void notificarResgateAluno(ResgateBeneficio resgate) {
        Aluno aluno = resgate.getAluno();
        String emailDestinatario = aluno.getEmail();
        
        String assunto = String.format("Resgate confirmado: %s", resgate.getBeneficio().getNome());
        String corpo = String.format("""
            Olá %s,
            
            Seu resgate foi processado com sucesso!
            
            Benefício: %s
            Custo: %.2f moedas
            Código de Resgate: %s
            
            Apresente este código na empresa parceira para retirar seu benefício.
            
            Seu saldo atual: %.2f moedas
            """,
            aluno.getNome(),
            resgate.getBeneficio().getNome(),
            resgate.getValorPago(),
            resgate.getCodigoResgate(),
            aluno.getSaldoMoedas()
        );
        
        enviarEmail(emailDestinatario, assunto, corpo);
        
        // Armazenar notificação no banco
        if (notificacaoRepository != null) {
            salvarNotificacao(aluno, "RESGATE_BENEFICIO", assunto, corpo, resgate.getCodigoResgate());
        }
    }
    
    /**
     * Notifica empresa quando aluno resgata benefício
     */
    public void notificarResgateEmpresa(ResgateBeneficio resgate) {
        String emailEmpresa = resgate.getBeneficio().getEmpresaParceira().getEmail();
        
        String assunto = String.format("Novo resgate: %s", resgate.getBeneficio().getNome());
        String corpo = String.format("""
            Olá %s,
            
            Um novo benefício foi resgatado!
            
            Benefício: %s
            Código de Resgate: %s
            Aluno: %s
            Data do Resgate: %s
            
            Aguarde o aluno apresentar o código para retirada.
            """,
            resgate.getBeneficio().getEmpresaParceira().getNome(),
            resgate.getBeneficio().getNome(),
            resgate.getCodigoResgate(),
            resgate.getAluno().getNome(),
            resgate.getDataResgate()
        );
        
        enviarEmail(emailEmpresa, assunto, corpo);
    }
    
    /**
     * Método auxiliar para enviar email
     * TODO: Implementar integração real
     */
    private void enviarEmail(String destinatario, String assunto, String corpo) {
        log.info("=== EMAIL ENVIADO ===");
        log.info("Para: {}", destinatario);
        log.info("Assunto: {}", assunto);
        log.info("Corpo:\n{}", corpo);
        log.info("====================");
        
        // TODO: Implementar envio real de email
        // Exemplo com JavaMail ou alguma API de terceiros
    }
    
    /**
     * Salva a notificação no banco de dados para histórico
     */
    private void salvarNotificacao(com.puc.moeda.models.Usuario usuario, String tipo, String assunto, String corpo, String codigoReferencia) {
        if (notificacaoRepository != null) {
            try {
                Notificacao notificacao = new Notificacao();
                notificacao.setUsuario(usuario);
                notificacao.setTipo(tipo);
                notificacao.setAssunto(assunto);
                notificacao.setCorpo(corpo);
                notificacao.setCodigoReferencia(codigoReferencia);
                notificacao.setLida(false);
                
                notificacaoRepository.save(notificacao);
            } catch (Exception e) {
                log.warn("Erro ao salvar notificação no banco: {}", e.getMessage());
            }
        }
    }
}
