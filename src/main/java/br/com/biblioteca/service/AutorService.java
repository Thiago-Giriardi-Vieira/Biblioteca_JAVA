package br.com.biblioteca.service;

import br.com.biblioteca.dto.AutorForm;
import br.com.biblioteca.model.Autor;
import br.com.biblioteca.repository.AutorRepository;
import br.com.biblioteca.repository.LivroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class AutorService {
    private final AutorRepository autorRepository;
    private final LivroRepository livroRepository;

    public AutorService(AutorRepository autorRepository, LivroRepository livroRepository) {
        this.autorRepository = autorRepository;
        this.livroRepository = livroRepository;
    }

    @Transactional(readOnly = true)
    public List<Autor> listar(String busca) {
        return busca == null || busca.isBlank()
            ? autorRepository.findAllByOrderByNomeAsc()
            : autorRepository.findByNomeContainingIgnoreCaseOrderByNomeAsc(busca.trim());
    }

    @Transactional(readOnly = true)
    public Autor buscar(Long id) {
        return autorRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Autor não encontrado."));
    }

    public Autor salvar(Long id, AutorForm form) {
        Autor autor = id == null ? new Autor() : buscar(id);
        autor.setNome(form.getNome().trim());
        autor.setNacionalidade(form.getNacionalidade().trim());
        autor.setDataNascimento(form.getDataNascimento());
        autor.setBiografia(form.getBiografia());
        byte[] imagem = ImagemService.validarEExtrair(form.getImagem());
        if (imagem != null) {
            autor.setImagem(imagem);
            autor.setTipoImagem(form.getImagem().getContentType());
        }
        return autorRepository.save(autor);
    }

    public void excluir(Long id) {
        Autor autor = buscar(id);
        if (livroRepository.existsByAutorId(id)) {
            throw new RegraNegocioException("Não é possível excluir um autor que possui livros vinculados.");
        }
        autorRepository.delete(autor);
    }
}
