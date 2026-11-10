package br.com.biblioteca.repository;

import br.com.biblioteca.model.Emprestimo;
import br.com.biblioteca.model.StatusEmprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    boolean existsByLivroIdAndStatusIn(Long livroId, Collection<StatusEmprestimo> status);
    boolean existsByLeitorIdAndStatus(Long leitorId, StatusEmprestimo status);
    boolean existsByLeitorId(Long leitorId);
    boolean existsByLivroId(Long livroId);
    List<Emprestimo> findByStatusAndDataDevolucaoPrevistaBefore(StatusEmprestimo status, LocalDate data);

    @Query("select e from Emprestimo e join fetch e.leitor join fetch e.livro order by e.dataEmprestimo desc, e.id desc")
    List<Emprestimo> listarComRelacionamentos();
}
