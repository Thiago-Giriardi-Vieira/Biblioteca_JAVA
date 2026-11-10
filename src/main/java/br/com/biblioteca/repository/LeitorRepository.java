package br.com.biblioteca.repository;

import br.com.biblioteca.model.Leitor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LeitorRepository extends JpaRepository<Leitor, Long> {
    Optional<Leitor> findByCpf(String cpf);
    Optional<Leitor> findByEmailIgnoreCase(String email);
}
