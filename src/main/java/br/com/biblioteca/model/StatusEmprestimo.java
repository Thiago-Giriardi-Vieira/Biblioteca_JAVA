package br.com.biblioteca.model;

public enum StatusEmprestimo {
    ATIVO("Ativo"),
    ATRASADO("Atrasado"),
    DEVOLVIDO("Devolvido");

    private final String descricao;

    StatusEmprestimo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
