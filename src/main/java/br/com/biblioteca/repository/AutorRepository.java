package br.com.biblioteca.repository;

import br.com.biblioteca.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    List<Autor> findAllByOrderByNomeAsc();
    List<Autor> findByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);
}
