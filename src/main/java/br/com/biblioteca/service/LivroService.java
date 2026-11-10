package br.com.biblioteca.service;

import br.com.biblioteca.dto.LivroForm;
import br.com.biblioteca.model.Livro;
import br.com.biblioteca.repository.AutorRepository;
import br.com.biblioteca.repository.EmprestimoRepository;
import br.com.biblioteca.repository.LivroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
public class LivroService {
    private final LivroRepository livroRepository;
    private final AutorRepository autorRepository;
    private final EmprestimoRepository emprestimoRepository;

    public LivroService(LivroRepository livroRepository, AutorRepository autorRepository, EmprestimoRepository emprestimoRepository) {
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
        this.emprestimoRepository = emprestimoRepository;
    }

    @Transactional(readOnly = true)
    public List<Livro> listar(String busca, String categoria) {
        String termo = busca == null ? "" : busca.toLowerCase().trim();
        String filtro = categoria == null ? "" : categoria.trim();
        return livroRepository.findAllByOrderByTituloAsc().stream()
            .filter(livro -> termo.isBlank() || livro.getTitulo().toLowerCase().contains(termo)
                || livro.getAutor().getNome().toLowerCase().contains(termo)
                || livro.getIsbn().contains(termo))
            .filter(livro -> filtro.isBlank() || filtro.equals(livro.getCategoria()))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Livro> listarPorAutor(Long autorId) {
        return livroRepository.findByAutorIdOrderByTituloAsc(autorId);
    }

    @Transactional(readOnly = true)
    public Livro buscar(Long id) {
        return livroRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Livro não encontrado."));
    }

    public Livro salvar(Long id, LivroForm form) {
        Livro livro = id == null ? new Livro() : buscar(id);
        String isbn = form.getIsbn().replaceAll("[^0-9Xx]", "").toUpperCase();
        if (!isbn.matches("(?:[0-9]{9}[0-9X]|[0-9]{13})")) {
            throw new RegraNegocioException("O ISBN precisa ter 10 ou 13 caracteres válidos.");
        }
        livroRepository.findByIsbn(isbn).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                throw new RegraNegocioException("Este ISBN já está cadastrado.");
            }
        });
        livro.setTitulo(form.getTitulo().trim());
        livro.setIsbn(isbn);
        livro.setAnoPublicacao(form.getAnoPublicacao());
        livro.setAutor(autorRepository.findById(form.getAutorId())
            .orElseThrow(() -> new RegraNegocioException("Selecione um autor válido.")));
        livro.setCategoria(form.getCategoria());
        livro.setDataCompra(form.getDataCompra());
        livro.setDescricao(form.getDescricao());
        byte[] imagem = ImagemService.validarEExtrair(form.getImagem());
        if (imagem != null) {
            livro.setImagem(imagem);
            livro.setTipoImagem(form.getImagem().getContentType());
        }
        return livroRepository.save(livro);
    }

    public void excluir(Long id) {
        Livro livro = buscar(id);
        if (emprestimoRepository.existsByLivroId(id)) {
            throw new RegraNegocioException("Não é possível excluir um livro que possui histórico de empréstimos.");
        }
        livroRepository.delete(livro);
    }
}
