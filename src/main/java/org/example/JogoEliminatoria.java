package org.example;

import java.util.ArrayList;
import java.util.List;

public class JogoEliminatoria implements IModalidadeJogo {
    private static final int MAXIMO_PARTICIPANTES = 32; // Múltiplo de 2 para eliminação

    private String nome = "Eliminatória";
    private String descricao = "Jogadores são eliminados progressivamente até sobrar o vencedor";
    private List<Jogador> jogadoresAtivos;
    private int rodadaEliminacao;
    private int perguntasParaEliminacao;

    public JogoEliminatoria() {
        this.jogadoresAtivos = new ArrayList<>();
        this.rodadaEliminacao = 1;
        this.perguntasParaEliminacao = 5; // A cada 5 perguntas, elimina jogadores
    }

    @Override
    public void iniciarJogo() {
        System.out.println("Iniciando jogo eliminatório...");
        System.out.println("Participantes iniciais: " + jogadoresAtivos.size());
        System.out.println("Meta: Eliminar jogadores até sobrar apenas 1 vencedor");
    }

    @Override
    public void processarResposta(Jogador jogador, Resposta resposta) {
        // Em jogo eliminatório, respostas erradas podem levar à eliminação

        if (!jogadoresAtivos.contains(jogador)) {
            System.out.println(jogador.getNome() + " já foi eliminado!");
            return;
        }

        if (resposta.isCorreta()) {
            System.out.println(jogador.getNome() + " acertou! +" + resposta.getPontosObtidos() + " pontos");
        } else if (resposta.isPulou()) {
            System.out.println(jogador.getNome() + " pulou a pergunta - RISCO DE ELIMINAÇÃO!");
            // Marcar jogador para possível eliminação
            marcarParaEliminacao(jogador);
        } else {
            System.out.println(jogador.getNome() + " errou - RISCO DE ELIMINAÇÃO!");
            // Marcar jogador para possível eliminação
            marcarParaEliminacao(jogador);
        }

        // Verificar se é hora de eliminar jogadores
        if (deveEliminarJogadores()) {
            eliminarJogadores();
        }
    }

    @Override
    public void finalizarJogo() {
        System.out.println("Finalizando jogo eliminatório...");

        if (jogadoresAtivos.size() == 1) {
            Jogador vencedor = jogadoresAtivos.get(0);
            System.out.println("🏆 VENCEDOR: " + vencedor.getNome());
            vencedor.registrarVitoria();
        } else if (jogadoresAtivos.size() > 1) {
            System.out.println("Empate entre " + jogadoresAtivos.size() + " jogadores:");
            for (Jogador jogador : jogadoresAtivos) {
                System.out.println("- " + jogador.getNome());
                jogador.registrarVitoria(); // Todos os sobreviventes são vencedores
            }
        }
    }

    @Override
    public List<ResultadoJogo> calcularResultados() {
        List<ResultadoJogo> resultados = new ArrayList<>();

        // Implementar cálculo específico para eliminatória
        // Considerar ordem de eliminação, rodada em que foi eliminado, etc.

        return resultados;
    }

    @Override
    public boolean podeAdicionarParticipante(Jogador jogador) {
        if (jogador == null || !jogador.isAtivo()) {
            return false;
        }

        // Verificar se o número é potência de 2 para facilitar eliminação
        int novoTotal = jogadoresAtivos.size() + 1;
        return novoTotal <= MAXIMO_PARTICIPANTES && isPotenciaDe2(novoTotal);
    }

    @Override
    public int getMaximoParticipantes() {
        return MAXIMO_PARTICIPANTES;
    }

    @Override
    public String getDescricaoModalidade() {
        return descricao;
    }

    // Métodos específicos de eliminatória
    public void adicionarJogadorAtivo(Jogador jogador) {
        if (!jogadoresAtivos.contains(jogador)) {
            jogadoresAtivos.add(jogador);
        }
    }

    public void eliminarJogador(Jogador jogador) {
        if (jogadoresAtivos.remove(jogador)) {
            System.out.println("❌ " + jogador.getNome() + " foi ELIMINADO na rodada " + rodadaEliminacao);
        }
    }

    private void marcarParaEliminacao(Jogador jogador) {
        // Implementar sistema de marcação para eliminação
        // Por exemplo, acumular erros/pulos
    }

    private boolean deveEliminarJogadores() {
        // Determinar se é momento de eliminar jogadores
        // Por exemplo, a cada X perguntas ou baseado em performance
        return rodadaEliminacao % perguntasParaEliminacao == 0 && jogadoresAtivos.size() > 1;
    }

    private void eliminarJogadores() {
        if (jogadoresAtivos.size() <= 1) {
            return;
        }

        // Eliminar jogadores com pior performance
        int numeroAEliminar = Math.max(1, jogadoresAtivos.size() / 4); // Elimina 25%

        // Ordenar por pontuação (menor primeiro)
        jogadoresAtivos.sort((j1, j2) -> Integer.compare(j1.getPontuacaoTotal(), j2.getPontuacaoTotal()));

        List<Jogador> eliminados = new ArrayList<>();
        for (int i = 0; i < numeroAEliminar && i < jogadoresAtivos.size() - 1; i++) {
            eliminados.add(jogadoresAtivos.get(i));
        }

        for (Jogador eliminado : eliminados) {
            eliminarJogador(eliminado);
        }

        rodadaEliminacao++;

        System.out.println("Jogadores restantes: " + jogadoresAtivos.size());
    }

    private boolean isPotenciaDe2(int numero) {
        return numero > 0 && (numero & (numero - 1)) == 0;
    }

    public List<Jogador> getJogadoresAtivos() {
        return new ArrayList<>(jogadoresAtivos);
    }

    public int getRodadaEliminacao() {
        return rodadaEliminacao;
    }

    public int getPerguntasParaEliminacao() {
        return perguntasParaEliminacao;
    }

    public void setPerguntasParaEliminacao(int perguntasParaEliminacao) {
        this.perguntasParaEliminacao = Math.max(1, perguntasParaEliminacao);
    }

    public String getNome() { return nome; }

    @Override
    public String toString() {
        return nome + " - " + descricao;
    }
}
