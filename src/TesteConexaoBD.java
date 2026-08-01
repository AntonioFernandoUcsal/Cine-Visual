
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TesteConexaoBD {
    public static void main(String[] args) {
        
       
        String url = "jdbc:postgresql://localhost:5432/Aluguel"; 
        String usuario = "postgres";
        String senha = "cb694348";


        try {
           
            Class.forName("org.postgresql.Driver");

           
            try (Connection conexao = DriverManager.getConnection(url, usuario, senha)) {
                System.out.println("=> SUCESSO: O VS Code conectou com o PostgreSQL!");
                
                
                String sql = "SELECT * FROM vw_locacoes_ativas";
                try (PreparedStatement stmt = conexao.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
                    
                
                    
                    
                    while (rs.next()) {
                        int id = rs.getInt("id_locacao");
                        String cliente = rs.getString("nome_cliente");
                        String equipamento = rs.getString("modelo_equipamento");
                        double total = rs.getDouble("valor_total");
                        
                        System.out.println("Contrato Nº: " + id +
                                " | Cliente: " + cliente +
                                " | Equipamento: " + equipamento +
                                " | Total: R$ " + total);
                    }
                    
                    
                }
            }
            

        } catch (ClassNotFoundException e) {
            System.err.println("ERRO: O Driver JDBC do PostgreSQL não foi encontrado");
            System.err.println("Certifique-se de adicionar o arquivo .jar nas 'Referenced Libraries'.");
            
        } catch (SQLException e) {
            System.err.println("ERRO: Falha ao conectar ou executar comando no banco de dados.");
            System.err.println("Verifique se a senha está correta e se o servidor está ativo.");
            
        }
    }
}