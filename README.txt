# 🎬 CineRental Manager (Sistema Cine Visual)

Sistema de gestão de aluguer de equipamentos audiovisuais (câmaras, lentes, iluminação, áudio) desenvolvido em Java com integração a uma base de dados relacional PostgreSQL.

Este projeto foi desenvolvido como **Trabalho Final da disciplina de Banco de Dados**, cumprindo todos os requisitos de modelagem, scripts DDL/DML, e integração via JDBC.

🎯 Objetivo do Sistema
Gerir de forma centralizada o catálogo de equipamentos, o registo de clientes e o ciclo de vida dos contratos de locação. O sistema evita conflitos de agenda, automatiza o cálculo financeiro das diárias (com regras de fidelidade) e processa multas por atraso na devolução.

🛠️ Tecnologias e Ferramentas Utilizadas
* **Linguagem:** Java 21
* **Base de Dados:** PostgreSQL 16
* **Gestor de BD:** pgAdmin 4
* **Integração:** JDBC (Driver `postgresql-42.7.3.jar`)
* **IDE:** Visual Studio Code

📋 Requisitos Académicos Cumpridos
O sistema implementa com sucesso:
- [x] Mínimo de 5 entidades no banco de dados.
- [x] CRUD completo em Java (JDBC).
- [x] **Regras de Negócio:** Controlo de disponibilidade de equipamentos, descontos de fidelidade e multas por atraso.
- [x] **VIEW:** `vw_locacoes_ativas` (Utiliza múltiplos `JOINs` para exibir um relatório limpo de contratos em andamento).
- [x] **FUNCTION:** `fn_calcular_valor_locacao` (Realiza o cálculo real do valor do contrato multiplicando os dias pela diária e aplicando descontos).
- [x] **PROCEDURE:** `sp_registrar_devolucao` (Executa uma ação relevante no sistema: atualiza datas, liberta equipamentos e calcula multas automaticamente).

🚀 Como Executar o Projeto

1) Configurar a Base de Dados
1. Abra o **pgAdmin 4** e crie uma base de dados chamada `Aluguel`.
2. Abra a *Query Tool* nessa base de dados e execute o script SQL completo fornecido no relatório do projeto (criação de tabelas, view, function, procedure e os inserts iniciais).

2) Configurar o Projeto Java
1. Abra a pasta do projeto no **Visual Studio Code**.
2. Certifique-se de que possui a extensão *Extension Pack for Java* instalada.
3. O driver JDBC (`postgresql-42.7.3.jar`) já se encontra na pasta `lib`. O ficheiro `.vscode/settings.json` já está configurado para o reconhecer automaticamente.
4. Se a sua senha do PostgreSQL for diferente, abra os ficheiros `SistemaCineVisual.java` e `TesteConexaoBD.java` e atualize as credenciais:
   ```java
   String url = "jdbc:postgresql://localhost:5432/Aluguel";
   String usuario = "postgres";
   String senha = "sua_senha_aqui"; // Altere se necessário