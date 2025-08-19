package org.example;

public class Conquista {
    public enum TipoConquista {
        VITORIA, PERFORMANCE, CATEGORIA, ESPECIAL
    }

    private int id;
    private String nome;
    private String descricao;
    private TipoConquista tipo;
    private String icone;
    private int pontosRequeridos;
    private Categoria categoriaEspecifica;
    private boolean rara;

    public Conquista(String nome, String descricao, TipoConquista tipo,
                     int pontosRequeridos, boolean rara) {
        this.nome = nome;
        this.descricao = descricao;
        this.tipo = tipo;
        this.pontosRequeridos = pontosRequeridos;
        this.rara = rara;
    }

    public boolean verificarCriterio(Jogador jogador) {
        switch (tipo) {
            case VITORIA:
                return jogador.getNumeroVitorias() >= pontosRequeridos;
            case PERFORMANCE:
                return jogador.calcularTaxaVitoria() >= (pontosRequeridos / 100.0);
            case CATEGORIA:
                return categoriaEspecifica != null &&
                        jogador.getPontuacaoCategoria(categoriaEspecifica) >= pontosRequeridos;
            case ESPECIAL:
                return verificarCriterioEspecial(jogador);
            default:
                return false;
        }
    }

    private boolean verificarCriterioEspecial(Jogador jogador) {
        // Implementar critérios especiais como "Enciclopédia" (acertar em todas as categorias)
        return false;
    }

    // Getters/Setters
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public TipoConquista getTipo() { return tipo; }
    public boolean isRara() { return rara; }
    // ... outros getters
}

