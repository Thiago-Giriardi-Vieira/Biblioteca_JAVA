package br.com.biblioteca.dto;

import br.com.biblioteca.model.Leitor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LeitorForm {
    @NotBlank(message = "Informe o nome completo")
    @Size(max = 150)
    private String nome;

    @NotBlank(message = "Informe o CPF")
    private String cpf;

    @NotBlank(message = "Informe o e-mail")
    @Email(message = "E-mail inválido")
    @Size(max = 150)
    private String email;

    @NotBlank(message = "Informe o telefone")
    @Size(max = 20)
    private String telefone;

    public static LeitorForm de(Leitor leitor) {
        LeitorForm form = new LeitorForm();
        form.setNome(leitor.getNome());
        form.setCpf(leitor.getCpf());
        form.setEmail(leitor.getEmail());
        form.setTelefone(leitor.getTelefone());
        return form;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
}
