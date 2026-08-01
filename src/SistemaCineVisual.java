import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;


public class SistemaCineVisual {

    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/Aluguel";
        String usuario = "postgres";
        String senha = "cb694348";

        try (Connection conexao = DriverManager.getConnection(url, usuario, senha);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("=====================================");
            System.out.println("  CONECTADO AO CINE Visual!  ");
            System.out.println("=====================================");

            int opcao = -1;

           
            while (opcao != 0) {
                System.out.println("\n===== MENU PRINCIPAL =====");
                System.out.println("1. Listar");
                System.out.println("2. Cadastrar");
                System.out.println("3. Excluir");
                System.out.println("4. Gerenciar Locação");
                System.out.println("5. Criar Nova Locação (Contrato)");
                System.out.println("0. Sair");
                System.out.print("Escolha uma opção: ");               
                opcao = scanner.nextInt();
                System.out.print("");

                scanner.nextLine(); 

                switch (opcao) {
                    case 1 -> menuListar(conexao, scanner);
                    case 2 -> menuCadastrar(conexao, scanner);
                    case 3 -> menuExcluir(conexao, scanner);
                    case 4 -> menuAlterarLocacao(conexao, scanner);
                    case 5 -> criarNovaLocacao(conexao, scanner);
                    case 0 -> System.out.println("A encerrar o sistema... Até logo!");
                    default -> System.out.println("Opção inválida! Tente novamente.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro crítico de banco de dados: " + e.getMessage());
        }
    }

    

    private static void menuListar(Connection conexao, Scanner scanner) {
        int op = -1;
        while (op != 0) {
            System.out.print("\n");
            System.out.println("\n--- O QUE DESEJA LISTAR? ---");
            System.out.println("1. Clientes");
            System.out.println("2. Equipamentos (com Categorias)");
            System.out.println("3. Categorias");
            System.out.println("4. Locações Ativas (View)");
            System.out.println("0. Voltar ao Menu Principal");
            System.out.print("Escolha: ");
            
            op = scanner.nextInt();
            scanner.nextLine();

            switch (op) {
                case 1 -> listarClientes(conexao);
                case 2 -> listarEquipamentos(conexao);
                case 3 -> listarCategorias(conexao);
                case 4 -> listarLocacoesAtivas(conexao);
                case 0 -> System.out.println("A voltar ao Menu Principal...");
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private static void menuCadastrar(Connection conexao, Scanner scanner) {
        int op = -1;
        while (op != 0) {
            System.out.println("\n--- O QUE DESEJA CADASTRAR? ---");
            System.out.println("1. Cliente");
            System.out.println("2. Categoria");
            System.out.println("3. Equipamento");
            System.out.println("0. Voltar ao Menu Principal");
            System.out.print("Escolha: ");
            
            op = scanner.nextInt();
            scanner.nextLine();

            switch (op) {
                case 1 -> cadastrarCliente(conexao, scanner);
                case 2 -> cadastrarCategoria(conexao, scanner);
                case 3 -> cadastrarEquipamento(conexao, scanner);
                case 0 -> System.out.println("A voltar ao Menu Principal...");
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private static void menuExcluir(Connection conexao, Scanner scanner) {
        int op = -1;
        while (op != 0) {
            System.out.println("\n--- O QUE DESEJA EXCLUIR? ---");
            System.out.println("1. Cliente");
            System.out.println("2. Categoria");
            System.out.println("3. Equipamento");
            System.out.println("0. Voltar ao Menu Principal");
            System.out.print("Escolha: ");
            
            op = scanner.nextInt();
            scanner.nextLine();

            if (op == 0) {
                System.out.println("A voltar ao Menu Principal...");
                break;
            }

            if (op >= 1 && op <= 3) {
                System.out.print("Digite o ID do item que deseja excluir: ");
                int id = scanner.nextInt();
                scanner.nextLine();

                switch (op) {
                    case 1 -> excluirGenerico(conexao, "Cliente", id);
                    case 2 -> excluirGenerico(conexao, "Categoria", id);
                    case 3 -> excluirGenerico(conexao, "Equipamento", id);
                }
            } else {
                System.out.println("Opção inválida.");
            }
        }
    }

    
    // crud
    

    // listagens
    private static void listarClientes(Connection conexao) {
        System.out.println("\n-- CLIENTES --");
        String sql = "SELECT * FROM Cliente ORDER BY id";
        try (PreparedStatement stmt = conexao.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            boolean tem = false;
            while (rs.next()) {
                tem = true;
                System.out.println("ID: " + rs.getInt("id") + " | Nome: " + rs.getString("nome") + " | CPF: " + rs.getString("cpf"));
            }
            if (!tem) System.out.println("Nenhum cliente registado.");
        } catch (SQLException e) { System.err.println("Erro: " + e.getMessage()); }
         System.out.println("\n---------------");
    }

    private static void listarEquipamentos(Connection conexao) {
        System.out.println("\n-- EQUIPAMENTOS --");
        
        String sql = "SELECT e.id, e.modelo, c.nome AS categoria, e.status, e.valor_diaria " +
                     "FROM Equipamento e JOIN Categoria c ON e.id_categoria = c.id ORDER BY e.id";
                     
        try (PreparedStatement stmt = conexao.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            boolean tem = false;
            while (rs.next()) {
                tem = true;
                System.out.println("ID: " + rs.getInt("id") + 
                                   " | Modelo: " + rs.getString("modelo") + 
                                   " | Categoria: " + rs.getString("categoria") + 
                                   " | Status: " + rs.getString("status") +
                                   " | Diária: R$ " + String.format("%.2f", rs.getDouble("valor_diaria"))); // <-- VALOR AQUI
            }
            if (!tem) System.out.println("Nenhum equipamento cadastrado.");
        } catch (SQLException e) { 
            System.err.println("Erro: " + e.getMessage()); 
        }
        System.out.println("------------------\n");
    }

    private static void listarCategorias(Connection conexao) {
        System.out.println("\n-- CATEGORIAS --");
        String sql = "SELECT * FROM Categoria ORDER BY id";
        try (PreparedStatement stmt = conexao.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            boolean tem = false;
            while (rs.next()) {
                tem = true;
                System.out.println("ID: " + rs.getInt("id") + " | Nome: " + rs.getString("nome"));
            }
            if (!tem) System.out.println("Nenhuma categoria registada.");
        } catch (SQLException e) { System.err.println("Erro: " + e.getMessage()); }
        System.out.println("\n---------------");
    }

    private static void listarLocacoesAtivas(Connection conexao) {
        System.out.println("\n--- LOCAÇÕES ATIVAS (VIEW) ---");
        String sql = "SELECT * FROM vw_locacoes_ativas";
        try (PreparedStatement stmt = conexao.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            boolean tem = false;
            while (rs.next()) {
                tem = true;
                System.out.println("Contrato: " + rs.getInt("id_locacao") + 
                                   " | Cliente: " + rs.getString("nome_cliente") + 
                                   " | Equip: " + rs.getString("modelo_equipamento") +
                                   " | Retirada: " + rs.getDate("data_retirada") +
                                   " | Previsão: " + rs.getDate("data_devolucao_prevista") +
                                   " | Status: " + rs.getString("status_devolucao") +
                                   " | A PAGAR: R$ " + String.format("%.2f", rs.getDouble("valor_total"))); // <-- A MÁGICA ACONTECE AQUI
            }
            if (!tem) System.out.println("Nenhuma locação ativa no momento.");
        } catch (SQLException e) { 
            System.err.println("Erro: " + e.getMessage()); 
        }
        System.out.println("------------------------------\n");
    }

    
    private static void cadastrarCliente(Connection conexao, Scanner scanner) {
        System.out.print("ID (número): ");
        int id = scanner.nextInt(); scanner.nextLine();
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        System.out.print("Telefone: ");
        String telefone = scanner.nextLine();

        String sql = "INSERT INTO Cliente (id, nome, cpf, telefone) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id); stmt.setString(2, nome); stmt.setString(3, cpf); stmt.setString(4, telefone);
            stmt.executeUpdate();
            System.out.println("SUCESSO: Cliente registado com sucesso!");
        } catch (SQLException e) { System.err.println("Erro: " + e.getMessage()); }
    }

    private static void cadastrarCategoria(Connection conexao, Scanner scanner) {
        System.out.print("Nome da Nova Categoria: ");
        String nome = scanner.nextLine();

        String sql = "INSERT INTO Categoria (nome) VALUES (?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.executeUpdate();
            System.out.println("SUCESSO: Categoria registada com sucesso!");
        } catch (SQLException e) { System.err.println("Erro: " + e.getMessage()); }
    }

    private static void cadastrarEquipamento(Connection conexao, Scanner scanner) {
        System.out.println("\n--- CADASTRAR EQUIPAMENTO ---");
        System.out.print("Modelo do Equipamento: ");
        String modelo = scanner.nextLine();
        
        System.out.print("Número de Série: ");
        String numSerie = scanner.nextLine();
        
        System.out.print("Valor da Diária: ");
        double valor = scanner.nextDouble();
        
        System.out.print("ID da Categoria (Veja na lista de categorias): ");
        int idCat = scanner.nextInt();
        scanner.nextLine(); 

        String sql = "INSERT INTO Equipamento (modelo, numero_serie, valor_diaria, id_categoria) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, modelo);
            stmt.setString(2, numSerie);
            stmt.setDouble(3, valor);
            stmt.setInt(4, idCat);
            stmt.executeUpdate();
            System.out.println("SUCESSO: Equipamento registado com sucesso!");
            
        } catch (SQLException e) {
           
            if (e.getSQLState().equals("23505")) {
                System.out.println("\nERRO: Já existe um equipamento registado com o Número de Série '" + numSerie + "'!");
                System.out.println("Por favor, verifique a lista de equipamentos e tente novamente com um número diferente.");
            } else {
                System.err.println("Erro ao cadastrar equipamento: " + e.getMessage());
            }
        }
    }
    
    private static void excluirGenerico(Connection conexao, String tabela, int id) {
        String sql = "DELETE FROM " + tabela + " WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int afetadas = stmt.executeUpdate();
            if (afetadas > 0) System.out.println("SUCESSO: " + tabela + " removido com sucesso!");
            else System.out.println("AVISO: ID não encontrado na tabela " + tabela + ".");
        } catch (SQLException e) { 
            System.err.println("Erro ao excluir. O item pode estar a ser usado noutros contratos! Erro: " + e.getMessage()); 
        }
    }

    // procedure
    private static void registrarDevolucao(Connection conexao, Scanner scanner) {
        System.out.println("\n--- REGISTAR DEVOLUÇÃO ---");
        System.out.print("ID do Contrato (Locação): ");
        int id = scanner.nextInt(); scanner.nextLine();
        System.out.print("Data real da devolução (YYYY-MM-DD): ");
        String data = scanner.nextLine();

        String sql = "CALL sp_registrar_devolucao(?, ?::DATE)";
        try (CallableStatement stmt = conexao.prepareCall(sql)) {
            stmt.setInt(1, id); stmt.setString(2, data);
            stmt.execute();
            System.out.println("SUCESSO: Devolução registada. Status atualizado e multas calculadas (se houver atraso)!");
        } catch (SQLException e) { System.err.println("Erro: " + e.getMessage()); }
    }

    private static void criarNovaLocacao(Connection conexao, Scanner scanner) {
        System.out.println("\n--- CRIAR NOVA LOCAÇÃO ---");
        
        System.out.print("ID do Cliente: ");
        int idCliente = scanner.nextInt();
        
        System.out.print("ID do Equipamento: ");
        int idEquipamento = scanner.nextInt(); scanner.nextLine(); // limpar buffer
        
        System.out.print("Data de Retirada (YYYY-MM-DD): ");
        String dataRetirada = scanner.nextLine();
        
        System.out.print("Previsão de Devolução (YYYY-MM-DD): ");
        String dataPrevista = scanner.nextLine();
        
        System.out.print("O cliente tem direito a desconto? (true/false): ");
        boolean temDesconto = scanner.nextBoolean();

       
        double valorDiaria = 0.0;
        String sqlDiaria = "SELECT valor_diaria FROM Equipamento WHERE id = ?";
        try (PreparedStatement stmtDiaria = conexao.prepareStatement(sqlDiaria)) {
            stmtDiaria.setInt(1, idEquipamento);
            try (ResultSet rs = stmtDiaria.executeQuery()) {
                if (rs.next()) {
                    valorDiaria = rs.getDouble("valor_diaria");
                } else {
                    System.out.println("ERRO: Equipamento não encontrado!");
                    return;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar diária: " + e.getMessage());
            return;
        }

      
        String sqlLocacao = "INSERT INTO Locacao (data_retirada, data_devolucao_prevista, id_cliente, valor_total) " +
                            "VALUES (?::DATE, ?::DATE, ?, fn_calcular_valor_locacao(?::DATE, ?::DATE, ?::NUMERIC, ?)) RETURNING id";
        
        int novoIdLocacao = -1;
        try (PreparedStatement stmtLoc = conexao.prepareStatement(sqlLocacao)) {
            stmtLoc.setString(1, dataRetirada);
            stmtLoc.setString(2, dataPrevista);
            stmtLoc.setInt(3, idCliente);
            stmtLoc.setString(4, dataRetirada);
            stmtLoc.setString(5, dataPrevista);
            stmtLoc.setDouble(6, valorDiaria);
            stmtLoc.setBoolean(7, temDesconto);
            
            try (ResultSet rs = stmtLoc.executeQuery()) {
                if (rs.next()) novoIdLocacao = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao criar contrato: " + e.getMessage());
            return;
        }

        
        String sqlItem = "INSERT INTO Item_Locacao (id_locacao, id_equipamento, subtotal) VALUES (?, ?, 0)";
        String sqlUpdateEquip = "UPDATE Equipamento SET status = 'Alugado' WHERE id = ?";
        
        try (PreparedStatement stmtItem = conexao.prepareStatement(sqlItem);
             PreparedStatement stmtUpdate = conexao.prepareStatement(sqlUpdateEquip)) {
            
            stmtItem.setInt(1, novoIdLocacao);
            stmtItem.setInt(2, idEquipamento);
            stmtItem.executeUpdate();
            
            stmtUpdate.setInt(1, idEquipamento);
            stmtUpdate.executeUpdate();
            
            System.out.println("SUCESSO: Contrato Nº " + novoIdLocacao + " criado com sucesso!");
            
        } catch (SQLException e) {
            System.out.println("Erro ao ligar item ao contrato: " + e.getMessage());
        }
    }

    private static void menuAlterarLocacao(Connection conexao, Scanner scanner) {
        int op = -1;
        while (op != 0) {
            System.out.println("\n--- ALTERAR / GERENCIAR LOCAÇÃO ---");
            System.out.println("1. Atualizar (Registar Devolução e Mudar Status)");
            System.out.println("2. Excluir Contrato de Locação (Por ID)");
            System.out.println("0. Voltar ao Menu Principal");
            System.out.print("Escolha: ");
            
            op = scanner.nextInt();
            scanner.nextLine();

            switch (op) {
                case 1 -> registrarDevolucao(conexao, scanner);
                case 2 -> excluirLocacao(conexao, scanner);
                case 0 -> System.out.println("A voltar ao Menu Principal...");
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private static void excluirLocacao(Connection conexao, Scanner scanner) {
        System.out.println("\n--- EXCLUIR CONTRATO DE LOCAÇÃO ---");
        System.out.print("Digite o ID do Contrato que deseja excluir: ");
        int idLocacao = scanner.nextInt();
        scanner.nextLine(); 

       
        String sqlUpdateEquip = "UPDATE Equipamento SET status = 'Disponivel' WHERE id IN (SELECT id_equipamento FROM Item_Locacao WHERE id_locacao = ?)";
        
        
        String sqlDeleteLoc = "DELETE FROM Locacao WHERE id = ?";

        try (PreparedStatement stmtUpdate = conexao.prepareStatement(sqlUpdateEquip);
             PreparedStatement stmtDelete = conexao.prepareStatement(sqlDeleteLoc)) {
            
            
            stmtUpdate.setInt(1, idLocacao);
            stmtUpdate.executeUpdate();

           
            stmtDelete.setInt(1, idLocacao);
            int afetadas = stmtDelete.executeUpdate();

            if (afetadas > 0) {
                System.out.println("SUCESSO: Contrato Nº " + idLocacao + " excluído! Equipamentos devolvidos ao estoque.");
            } else {
                System.out.println("AVISO: Contrato Nº " + idLocacao + " não encontrado.");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao excluir o contrato: " + e.getMessage());
        }
    }
}