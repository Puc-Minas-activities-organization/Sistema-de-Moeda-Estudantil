package com.puc.moeda.services;

import com.puc.moeda.config.EmailProperties;
import com.puc.moeda.models.Aluno;
import com.puc.moeda.models.ResgateBeneficio;
import com.puc.moeda.models.TransacaoMoeda;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * Serviço de notificação por email
 * Envia emails HTML profissionais para alunos, professores e empresas parceiras
 */
@Service
@Slf4j
public class NotificacaoService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private EmailProperties emailProperties;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
    
    /**
     * Notifica aluno quando recebe moedas
     */
    public void notificarRecebimentoMoedas(TransacaoMoeda transacao) {
        Aluno aluno = transacao.getDestinatario();
        String emailDestinatario = aluno.getEmail();
        
        String assunto = String.format("🪙 Você recebeu %.0f moedas!", transacao.getValor());
        String corpo = gerarEmailRecebimentoMoedas(transacao);
        
        enviarEmailHtml(emailDestinatario, assunto, corpo);
    }
    
    /**
     * Notifica aluno quando resgata um benefício (com código de cupom)
     */
    public void notificarResgateAluno(ResgateBeneficio resgate) {
        Aluno aluno = resgate.getAluno();
        String emailDestinatario = aluno.getEmail();
        
        String assunto = String.format("🎁 Cupom de Resgate: %s", resgate.getBeneficio().getNome());
        String corpo = gerarEmailCupomAluno(resgate);
        
        enviarEmailHtml(emailDestinatario, assunto, corpo);
    }
    
    /**
     * Notifica empresa quando aluno resgata benefício
     */
    public void notificarResgateEmpresa(ResgateBeneficio resgate) {
        String emailEmpresa = resgate.getBeneficio().getEmpresaParceira().getEmail();
        
        String assunto = String.format("📦 Novo Resgate: %s", resgate.getBeneficio().getNome());
        String corpo = gerarEmailNotificacaoEmpresa(resgate);
        
        enviarEmailHtml(emailEmpresa, assunto, corpo);
    }
    
    // ==================== TEMPLATES HTML ====================
    
    /**
     * Gera HTML para email de recebimento de moedas
     */
    private String gerarEmailRecebimentoMoedas(TransacaoMoeda transacao) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .highlight { background: #fff3cd; padding: 15px; border-left: 4px solid #ffc107; margin: 20px 0; border-radius: 5px; }
                    .amount { font-size: 32px; font-weight: bold; color: #28a745; text-align: center; margin: 20px 0; }
                    .info-box { background: white; padding: 15px; margin: 15px 0; border-radius: 5px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                    .footer { text-align: center; color: #666; font-size: 12px; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🪙 Moedas Recebidas!</h1>
                    </div>
                    <div class="content">
                        <p>Olá <strong>%s</strong>,</p>
                        
                        <div class="amount">+ %.0f moedas</div>
                        
                        <div class="info-box">
                            <p><strong>👨‍🏫 De:</strong> Professor(a) %s</p>
                            <p><strong>💬 Motivo:</strong></p>
                            <div class="highlight">%s</div>
                        </div>
                        
                        <div class="info-box">
                            <p><strong>💰 Seu novo saldo:</strong> <span style="color: #28a745; font-size: 20px; font-weight: bold;">%.0f moedas</span></p>
                        </div>
                        
                        <p style="text-align: center; margin-top: 30px;">
                            <a href="#" style="background: #667eea; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block;">
                                Ver Extrato Completo
                            </a>
                        </p>
                        
                        <div class="footer">
                            <p>Sistema de Moeda Estudantil</p>
                            <p>Este é um email automático, não responda.</p>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """,
            transacao.getDestinatario().getNome(),
            transacao.getValor(),
            transacao.getRemetente().getNome(),
            transacao.getMensagem(),
            transacao.getDestinatario().getSaldoMoedas()
        );
    }
    
    /**
     * Gera HTML para cupom de resgate do aluno
     */
    private String gerarEmailCupomAluno(ResgateBeneficio resgate) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #f093fb 0%%, #f5576c 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .cupom { background: white; border: 3px dashed #f5576c; padding: 20px; margin: 20px 0; text-align: center; border-radius: 10px; }
                    .codigo { font-size: 36px; font-weight: bold; color: #f5576c; letter-spacing: 3px; margin: 15px 0; font-family: monospace; }
                    .info-box { background: white; padding: 15px; margin: 15px 0; border-radius: 5px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                    .alert { background: #d1ecf1; border-left: 4px solid #0c5460; padding: 15px; margin: 15px 0; border-radius: 5px; }
                    .footer { text-align: center; color: #666; font-size: 12px; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎁 Seu Cupom de Resgate</h1>
                    </div>
                    <div class="content">
                        <p>Olá <strong>%s</strong>,</p>
                        
                        <p>Seu resgate foi processado com sucesso! 🎉</p>
                        
                        <div class="cupom">
                            <h2 style="margin-top: 0; color: #f5576c;">CÓDIGO DO CUPOM</h2>
                            <div class="codigo">%s</div>
                            <p style="font-size: 12px; color: #666;">Apresente este código na empresa parceira</p>
                        </div>
                        
                        <div class="info-box">
                            <p><strong>🎁 Benefício:</strong> %s</p>
                            <p><strong>🏢 Empresa:</strong> %s</p>
                            <p><strong>💰 Custo:</strong> %.0f moedas</p>
                            <p><strong>📅 Data do Resgate:</strong> %s</p>
                        </div>
                        
                        <div class="alert">
                            <strong>⚠️ Instruções Importantes:</strong>
                            <ul style="margin: 10px 0; padding-left: 20px;">
                                <li>Apresente este código na empresa parceira para retirar seu benefício</li>
                                <li>Guarde este email para referência futura</li>
                                <li>O código é válido conforme as condições da empresa</li>
                            </ul>
                        </div>
                        
                        <div class="info-box">
                            <p><strong>💰 Seu novo saldo:</strong> <span style="color: #28a745; font-size: 20px; font-weight: bold;">%.0f moedas</span></p>
                        </div>
                        
                        <div class="footer">
                            <p>Sistema de Moeda Estudantil</p>
                            <p>Este é um email automático, não responda.</p>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """,
            resgate.getAluno().getNome(),
            resgate.getCodigoResgate(),
            resgate.getBeneficio().getNome(),
            resgate.getBeneficio().getEmpresaParceira().getNome(),
            resgate.getValorPago(),
            resgate.getDataResgate().format(DATE_FORMATTER),
            resgate.getAluno().getSaldoMoedas()
        );
    }
    
    /**
     * Gera HTML para notificação de resgate para empresa
     */
    private String gerarEmailNotificacaoEmpresa(ResgateBeneficio resgate) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #4facfe 0%%, #00f2fe 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .codigo-box { background: white; border: 3px solid #4facfe; padding: 20px; margin: 20px 0; text-align: center; border-radius: 10px; }
                    .codigo { font-size: 36px; font-weight: bold; color: #4facfe; letter-spacing: 3px; margin: 15px 0; font-family: monospace; }
                    .info-box { background: white; padding: 15px; margin: 15px 0; border-radius: 5px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                    .alert { background: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 15px 0; border-radius: 5px; }
                    .footer { text-align: center; color: #666; font-size: 12px; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>📦 Novo Resgate Realizado</h1>
                    </div>
                    <div class="content">
                        <p>Olá <strong>%s</strong>,</p>
                        
                        <p>Um aluno resgatou um benefício da sua empresa! 🎉</p>
                        
                        <div class="codigo-box">
                            <h2 style="margin-top: 0; color: #4facfe;">CÓDIGO DE VALIDAÇÃO</h2>
                            <div class="codigo">%s</div>
                            <p style="font-size: 12px; color: #666;">Solicite este código ao aluno para validar o resgate</p>
                        </div>
                        
                        <div class="info-box">
                            <p><strong>🎁 Benefício Resgatado:</strong> %s</p>
                            <p><strong>👤 Aluno:</strong> %s</p>
                            <p><strong>📅 Data do Resgate:</strong> %s</p>
                            <p><strong>💰 Valor:</strong> %.0f moedas</p>
                        </div>
                        
                        <div class="alert">
                            <strong>⚠️ Próximos Passos:</strong>
                            <ul style="margin: 10px 0; padding-left: 20px;">
                                <li>Aguarde o aluno apresentar o código do cupom</li>
                                <li>Verifique se o código corresponde ao informado acima</li>
                                <li>Forneça o benefício ao aluno após validação</li>
                            </ul>
                        </div>
                        
                        <div class="footer">
                            <p>Sistema de Moeda Estudantil - Painel de Empresas Parceiras</p>
                            <p>Este é um email automático, não responda.</p>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """,
            resgate.getBeneficio().getEmpresaParceira().getNome(),
            resgate.getCodigoResgate(),
            resgate.getBeneficio().getNome(),
            resgate.getAluno().getNome(),
            resgate.getDataResgate().format(DATE_FORMATTER),
            resgate.getValorPago()
        );
    }
    
    // ==================== MÉTODOS AUXILIARES ====================
    
    /**
     * Envia email HTML usando JavaMailSender
     */
    private void enviarEmailHtml(String destinatario, String assunto, String corpoHtml) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(emailProperties.getFrom(), emailProperties.getFromName());
            helper.setTo(destinatario);
            helper.setSubject(assunto);
            helper.setText(corpoHtml, true); // true = HTML
            
            mailSender.send(message);
            
            log.info("✅ Email enviado com sucesso para: {}", destinatario);
            log.debug("Assunto: {}", assunto);
            
        } catch (MessagingException e) {
            log.error("❌ Erro ao enviar email para {}: {}", destinatario, e.getMessage());
            log.error("Assunto do email que falhou: {}", assunto);
        } catch (Exception e) {
            log.error("❌ Erro inesperado ao enviar email: {}", e.getMessage(), e);
        }
    }
}
