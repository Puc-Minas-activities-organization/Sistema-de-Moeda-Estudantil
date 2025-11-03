# Histórias de Usuário

## Autenticação e Cadastro

1. Eu como **usuário (Aluno, Professor ou Empresa Parceira)** quero realizar login no sistema para acessar minhas funcionalidades.
   - Retorna token JWT válido por 2 horas

2. Eu como **aluno** quero me cadastrar no sistema para receber moedas e resgatar benefícios.
   - Dados: nome, email, senha, CPF, RG, endereço, instituição e curso

3. Eu como **empresa parceira** quero me cadastrar no sistema para oferecer benefícios aos alunos.
   - Dados: nome da empresa, email, senha e CNPJ

## Professor

1. Eu como **professor** quero enviar moedas para um aluno para reconhecer seu desempenho.
   - Devo ter saldo suficiente
   - Devo informar: email do aluno, valor e motivo (obrigatório)
   - Aluno recebe notificação por email

2. Eu como **professor** quero consultar meu extrato de moedas para ver todas as transações que realizei.
   - Mostra: aluno, valor, data e motivo

3. Eu como **professor** quero consultar meu saldo de moedas.
   - Saldo renovado semestralmente (1000 moedas)
   - Saldo não utilizado é acumulado

4. Eu como **professor** quero consultar meu perfil.
   - Mostra: nome, email, CPF, departamento, instituição e saldo

## Aluno

1. Eu como **aluno** quero listar benefícios disponíveis para escolher qual resgatar.
   - Mostra apenas benefícios ativos
   - Mostra: nome, descrição, foto, custo e empresa parceira

2. Eu como **aluno** quero resgatar benefício usando minhas moedas.
   - Devo ter saldo suficiente
   - Sistema gera código único
   - Recebo email com código
   - Empresa recebe notificação
   - Status inicial: PENDENTE

3. Eu como **aluno** quero consultar meu extrato para ver moedas recebidas e gastas.
    - Mostra recebimentos (professor, valor, data, motivo)
    - Mostra resgates (benefício, valor, data, código)

4. Eu como **aluno** quero consultar meus resgates de benefícios.
    - Mostra: benefício, código, valor, data, status
    - Status: PENDENTE, USADO ou CANCELADO

5. Eu como **aluno** quero consultar meu saldo de moedas.

6. Eu como **aluno** quero consultar meu perfil.
    - Mostra: nome, email, CPF, RG, endereço, curso, instituição e saldo

7. Eu como **aluno** quero atualizar meu perfil.
    - Posso alterar: nome, endereço e curso
    - Não posso alterar: CPF, RG, email

## Empresa Parceira

1. Eu como **empresa parceira** quero cadastrar benefício.
    - Dados: nome, descrição, foto (URL), custo em moedas
    - Criado como ativo por padrão

2. Eu como **empresa parceira** quero editar benefício.
    - Posso editar apenas meus benefícios
    - Posso alterar: nome, descrição, foto, custo, status

3. Eu como **empresa parceira** quero remover benefício.
    - Posso remover apenas meus benefícios
    - Benefício é removido permanentemente

4. Eu como **empresa parceira** quero consultar meus benefícios.
    - Mostra apenas benefícios da minha empresa

5. Eu como **empresa parceira** quero consultar detalhes de um benefício específico.
    - Mostra: nome, descrição, foto, custo, status

6. Eu como **empresa parceira** quero consultar resgates dos meus benefícios.
    - Mostra: aluno, benefício, código, valor, data, status

7. Eu como **empresa parceira** quero confirmar uso de resgate pelo código.
    - Devo fornecer o código do resgate
    - Sistema valida se resgate pertence à minha empresa
    - Resgate é marcado como USADO com data de utilização

8. Eu como **empresa parceira** quero receber email quando benefício for resgatado.
    - Email contém: aluno, benefício, código e data
    - Enviado automaticamente após resgate

9. Eu como **empresa parceira** quero consultar meu perfil.
    - Mostra: nome da empresa, email, CNPJ

10. Eu como **empresa parceira** quero atualizar perfil da empresa.
    - Posso alterar: nome da empresa
    - Não posso alterar: CNPJ, email

## Notificações por Email

1. Eu como **aluno** quero receber email quando professor me enviar moedas.
    - Email contém: professor, valor, motivo e data
    - Enviado automaticamente

2. Eu como **aluno** quero receber email com código ao resgatar benefício.
    - Email contém: benefício, código único, empresa e instruções
    - Enviado automaticamente
