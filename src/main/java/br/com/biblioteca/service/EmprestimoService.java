package br.com.biblioteca.service;

import br.com.biblioteca.dto.EmprestimoForm;
import br.com.biblioteca.model.*;
import br.com.biblioteca.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmprestimoService {
    private static final List<StatusEmprestimo> EM_ABERTO = List.of(StatusEmprestimo.ATIVO, StatusEmprestimo.ATRASADO);
    private final EmprestimoRepository emprestimoRepository;
    private final LivroRepository livroRepository;
    private final LeitorRepository leitorRepository;

    public EmprestimoService(EmprestimoRepository emprestimoRepository, LivroRepository livroRepository, LeitorRepository leitorRepository) {
        this.emprestimoRepository = emprestimoRepository;
        this.livroRepository = livroRepository;
        this.leitorRepository = leitorRepository;
    }

    public void atualizarAtrasos() {
        List<Emprestimo> vencidos = emprestimoRepository.findByStatusAndDataDevolucaoPrevistaBefore(StatusEmprestimo.ATIVO, LocalDate.now());
        vencidos.forEach(e -> e.setStatus(StatusEmprestimo.ATRASADO));
        emprestimoRepository.saveAll(vencidos);
    }

    public List<Emprestimo> listar(String busca, String status) {
        atualizarAtrasos();
        String termo = busca == null ? "" : busca.toLowerCase().trim();
        return emprestimoRepository.listarComRelacionamentos().stream()
            .filter(e -> termo.isBlank() || e.getLeitor().getNome().toLowerCase().contains(termo)
                || e.getLivro().getTitulo().toLowerCase().contains(termo))
            .filter(e -> status == null || status.isBlank() || e.getStatus().name().equals(status))
            .collect(Collectors.toList());
    }

    public Emprestimo buscar(Long id) {
        atualizarAtrasos();
        return emprestimoRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Empréstimo não encontrado."));
    }

    public List<Livro> livrosDisponiveis() {
        atualizarAtrasos();
        return livroRepository.findAllByOrderByTituloAsc().stream()
            .filter(livro -> !emprestimoRepository.existsByLivroIdAndStatusIn(livro.getId(), EM_ABERTO))
            .collect(Collectors.toList());
    }

    public Emprestimo registrar(EmprestimoForm form) {
        atualizarAtrasos();
        if (form.getDataEmprestimo() == null || form.getDataDevolucaoPrevista() == null ||
            !form.getDataDevolucaoPrevista().isAfter(form.getDataEmprestimo())) {
            throw new RegraNegocioException("A devolução prevista deve ser posterior à data do empréstimo.");
        }
        if (form.getDataEmprestimo().isAfter(LocalDate.now())) {
            throw new RegraNegocioException("A data do empréstimo não pode estar no futuro.");
        }
        if (form.getDataDevolucaoPrevista().isBefore(LocalDate.now())) {
            throw new RegraNegocioException("A devolução prevista não pode estar no passado.");
        }
        Leitor leitor = leitorRepository.findById(form.getLeitorId())
            .orElseThrow(() -> new RegraNegocioException("Leitor não encontrado."));
        Livro livro = livroRepository.buscarParaEmprestimo(form.getLivroId())
            .orElseThrow(() -> new RegraNegocioException("Livro não encontrado."));
        if (emprestimoRepository.existsByLeitorIdAndStatus(leitor.getId(), StatusEmprestimo.ATRASADO)) {
            throw new RegraNegocioException("Leitor com empréstimo em atraso. Regularize antes de registrar um novo.");
        }
        if (emprestimoRepository.existsByLivroIdAndStatusIn(livro.getId(), EM_ABERTO)) {
            throw new RegraNegocioException("Livro indisponível. Ele já está emprestado.");
        }
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setLeitor(leitor);
        emprestimo.setLivro(livro);
        emprestimo.setDataEmprestimo(form.getDataEmprestimo());
        emprestimo.setDataDevolucaoPrevista(form.getDataDevolucaoPrevista());
        emprestimo.setStatus(StatusEmprestimo.ATIVO);
        return emprestimoRepository.save(emprestimo);
    }

    public void devolver(Long id, LocalDate data) {
        Emprestimo emprestimo = buscar(id);
        if (emprestimo.getStatus() == StatusEmprestimo.DEVOLVIDO) {
            throw new RegraNegocioException("Este empréstimo já foi devolvido.");
        }
        if (data == null || data.isBefore(emprestimo.getDataEmprestimo()) || data.isAfter(LocalDate.now())) {
            throw new RegraNegocioException("Informe uma data de devolução válida.");
        }
        emprestimo.setDataDevolucaoReal(data);
        emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO);
        emprestimoRepository.save(emprestimo);
    }

    public long diasAtraso(Emprestimo emprestimo, LocalDate data) {
        return Math.max(0, ChronoUnit.DAYS.between(emprestimo.getDataDevolucaoPrevista(), data));
    }

    public long contar(StatusEmprestimo status) {
        atualizarAtrasos();
        return emprestimoRepository.listarComRelacionamentos().stream().filter(e -> e.getStatus() == status).count();
    }
}
