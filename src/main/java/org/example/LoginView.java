package org.example;

import java.util.List;

public interface LoginView {
    void exibirErro(String mensagem);
    void exibirErros(List<String> erros);
    void exibirSucesso(String mensagem);
    void redirecionarParaMenuPrincipal();
    void limparFormulario();
}
