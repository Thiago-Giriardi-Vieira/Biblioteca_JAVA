package br.com.biblioteca.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

public class EmprestimoForm {
    @NotNull(message = "Selecione o leitor")
    private Long leitorId;

    @NotNull(message = "Selecione o livro")
    private Long livroId;

    @NotNull(message = "Informe a data do empréstimo")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataEmprestimo = LocalDate.now();

    @NotNull(message = "Informe a data de devolução prevista")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataDevolucaoPrevista = LocalDate.now().plusDays(14);

    public Long getLeitorId() { return leitorId; }
    public void setLeitorId(Long leitorId) { this.leitorId = leitorId; }
    public Long getLivroId() { return livroId; }
    public void setLivroId(Long livroId) { this.livroId = livroId; }
    public LocalDate getDataEmprestimo() { return dataEmprestimo; }
    public void setDataEmprestimo(LocalDate dataEmprestimo) { this.dataEmprestimo = dataEmprestimo; }
    public LocalDate getDataDevolucaoPrevista() { return dataDevolucaoPrevista; }
    public void setDataDevolucaoPrevista(LocalDate dataDevolucaoPrevista) { this.dataDevolucaoPrevista = dataDevolucaoPrevista; }
}
