package br.com.biblioteca.dto;

import br.com.biblioteca.model.Livro;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;

public class LivroForm {
    @NotBlank(message = "Informe o título")
    @Size(max = 200)
    private String titulo;

    @NotBlank(message = "Informe o ISBN")
    @Size(max = 20)
    private String isbn;

    @NotNull(message = "Informe o ano de publicação")
    @Min(value = 1000, message = "Ano de publicação inválido")
    @Max(value = 2200, message = "Ano de publicação inválido")
    private Integer anoPublicacao;

    @NotNull(message = "Selecione um autor")
    private Long autorId;

    private String categoria;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataCompra;

    @Size(max = 3000)
    private String descricao;

    private MultipartFile imagem;

    public static LivroForm de(Livro livro) {
        LivroForm form = new LivroForm();
        form.setTitulo(livro.getTitulo());
        form.setIsbn(livro.getIsbn());
        form.setAnoPublicacao(livro.getAnoPublicacao());
        form.setAutorId(livro.getAutor().getId());
        form.setCategoria(livro.getCategoria());
        form.setDataCompra(livro.getDataCompra());
        form.setDescricao(livro.getDescricao());
        return form;
    }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public Integer getAnoPublicacao() { return anoPublicacao; }
    public void setAnoPublicacao(Integer anoPublicacao) { this.anoPublicacao = anoPublicacao; }
    public Long getAutorId() { return autorId; }
    public void setAutorId(Long autorId) { this.autorId = autorId; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public LocalDate getDataCompra() { return dataCompra; }
    public void setDataCompra(LocalDate dataCompra) { this.dataCompra = dataCompra; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public MultipartFile getImagem() { return imagem; }
    public void setImagem(MultipartFile imagem) { this.imagem = imagem; }
}
