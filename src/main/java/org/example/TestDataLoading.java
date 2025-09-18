package org.example;

public class TestDataLoading {
    public static void main(String[] args) {
        System.out.println("=== TESTE DE CARREGAMENTO DE DADOS ===");

        // Criar instância do sistema
        SistemaQuiz sistema = SistemaQuiz.getInstance();

        // Carregar dados
        GerenciadorDados gerenciador = new GerenciadorDados(sistema);
        gerenciador.carregarDadosIniciais();

        // Verificar dados carregados
        System.out.println("\n=== RESULTADO DO CARREGAMENTO ===");
        System.out.println("Usuários carregados: " + sistema.getUsuarios().size());
        System.out.println("Jogos carregados: " + sistema.getJogos().size());
        System.out.println("Categorias carregadas: " + sistema.getCategorias().size());

        // Listar usuários
        System.out.println("\n=== USUÁRIOS ===");
        for (Usuario usuario : sistema.getUsuarios()) {
            System.out.println("- " + usuario.getTipoUsuario() + ": " + usuario.getNome() + " (" + usuario.getEmail() + ")");
        }

        // Listar jogos
        System.out.println("\n=== JOGOS ===");
        for (Jogo jogo : sistema.getJogos()) {
            System.out.println("- Jogo " + jogo.getId() + ": " + jogo.getNome() + " (Criador: " + jogo.getCriador().getNome() + ")");
        }

        // Testar busca de usuário
        System.out.println("\n=== TESTE DE BUSCA DE USUÁRIO ===");
        Usuario jogadorTeste = sistema.buscarUsuarioPorEmail("teste@email.com");
        if (jogadorTeste != null) {
            System.out.println("Usuário encontrado: " + jogadorTeste.getNome());

            if (jogadorTeste instanceof Jogador) {
                Jogador jogador = (Jogador) jogadorTeste;
                System.out.println("Jogos inscritos: " + jogador.getJogosInscritos().size());
                System.out.println("Jogos designados: " + jogador.getJogosDesignados().size());

                System.out.println("\n=== JOGOS DESIGNADOS ===");
                for (Object obj : jogador.getJogosDesignados()) {
                    if (obj instanceof Jogo) {
                        Jogo jogo = (Jogo) obj;
                        System.out.println("- " + jogo.getNome());
                    }
                }
            }
        } else {
            System.out.println("Usuário teste@email.com não encontrado!");
        }
    }
}
