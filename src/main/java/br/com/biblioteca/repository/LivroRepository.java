package br.com.biblioteca.repository;

import br.com.biblioteca.model.Livro;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface LivroRepository extends JpaRepository<Livro, Long> {
    List<Livro> findAllByOrderByTituloAsc();
    List<Livro> findByAutorIdOrderByTituloAsc(Long autorId);
    boolean existsByAutorId(Long autorId);
    Optional<Livro> findByIsbn(String isbn);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from Livro l where l.id = :id")
    Optional<Livro> buscarParaEmprestimo(@Param("id") Long id);
}
