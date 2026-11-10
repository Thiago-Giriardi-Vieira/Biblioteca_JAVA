package br.com.biblioteca.dto;

import br.com.biblioteca.model.Autor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;

public class AutorForm {
    @NotBlank(message = "Informe o nome do autor")
    @Size(max = 150)
    private String nome;

    @NotBlank(message = "Informe a nacionalidade")
    @Size(max = 100)
    private String nacionalidade;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataNascimento;

    @Size(max = 2000)
    private String biografia;

    private MultipartFile imagem;

    public static AutorForm de(Autor autor) {
        AutorForm form = new AutorForm();
        form.setNome(autor.getNome());
        form.setNacionalidade(autor.getNacionalidade());
        form.setDataNascimento(autor.getDataNascimento());
        form.setBiografia(autor.getBiografia());
        return form;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getNacionalidade() { return nacionalidade; }
    public void setNacionalidade(String nacionalidade) { this.nacionalidade = nacionalidade; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public String getBiografia() { return biografia; }
    public void setBiografia(String biografia) { this.biografia = biografia; }
    public MultipartFile getImagem() { return imagem; }
    public void setImagem(MultipartFile imagem) { this.imagem = imagem; }
}
