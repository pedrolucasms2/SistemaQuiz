package org.example;

import java.util.ArrayList;
import java.util.List;

public class LoginControler {
    private SistemaQuiz sistema;
    private LoginView view;

    public LoginControler(LoginView view) {
        this.view = view;
        this.sistema = SistemaQuiz.getInstance();
    }

    public boolean processarLogin(String email, String senha) {
        try {
            // Validações de interface
            if (!ValidadorInterface.validarEmail(email)) {
                view.exibirErro("Email inválido");
                return false;
            }

            if (!ValidadorInterface.validarSenha(senha)) {
                view.exibirErro("Senha deve ter pelo menos 6 caracteres");
                return false;
            }

            boolean sucesso = sistema.autenticar(email, senha);
            if (sucesso) {
                view.redirecionarParaMenuPrincipal();
            } else {
                view.exibirErro("Credenciais inválidas");
            }
            return sucesso;

        } catch (Exception e) {
            view.exibirErro("Erro interno: " + e.getMessage());
            return false;
        }
    }

    public void processarCadastro(String nome, String email, String senha, String confirmaSenha) {
        try {
            // Validações específicas de cadastro
            List<String> erros = validarDadosCadastro(nome, email, senha, confirmaSenha);
            if (!erros.isEmpty()) {
                view.exibirErros(erros);
                return;
            }

            // CORRIGIDO: cadastrarJogador pode lançar EmailJaExisteException
            sistema.cadastrarJogador(nome, email, senha);
            view.exibirSucesso("Cadastro realizado com sucesso!");
            view.limparFormulario();

        } catch (EmailJaExisteException e) { // AGORA A EXCEPTION PODE SER LANÇADA
            view.exibirErro("Este email já está cadastrado");
        } catch (Exception e) {
            view.exibirErro("Erro ao cadastrar: " + e.getMessage());
        }
    }

    private List<String> validarDadosCadastro(String nome, String email, String senha, String confirmaSenha) {
        List<String> erros = new ArrayList<>();

        if (nome == null || nome.trim().isEmpty()) {
            erros.add("Nome é obrigatório");
        }

        if (!ValidadorInterface.validarEmail(email)) {
            erros.add("Email inválido");
        }

        if (!ValidadorInterface.validarSenha(senha)) {
            erros.add("Senha deve ter pelo menos 6 caracteres");
        }

        if (!senha.equals(confirmaSenha)) {
            erros.add("Senhas não coincidem");
        }

        return erros;
    }
}

