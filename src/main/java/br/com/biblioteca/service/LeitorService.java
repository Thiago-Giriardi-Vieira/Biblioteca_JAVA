package br.com.biblioteca.service;

import br.com.biblioteca.dto.LeitorForm;
import br.com.biblioteca.model.Leitor;
import br.com.biblioteca.repository.EmprestimoRepository;
import br.com.biblioteca.repository.LeitorRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
public class LeitorService {
    private final LeitorRepository leitorRepository;
    private final EmprestimoRepository emprestimoRepository;

    public LeitorService(LeitorRepository leitorRepository, EmprestimoRepository emprestimoRepository) {
        this.leitorRepository = leitorRepository;
        this.emprestimoRepository = emprestimoRepository;
    }

    @Transactional(readOnly = true)
    public List<Leitor> listar(String busca, String ordem) {
        Sort sort = "desc".equals(ordem) ? Sort.by("nome").descending() : Sort.by("nome").ascending();
        String termo = busca == null ? "" : busca.toLowerCase().trim();
        return leitorRepository.findAll(sort).stream()
            .filter(leitor -> termo.isBlank() || leitor.getNome().toLowerCase().contains(termo)
                || leitor.getCpf().contains(termo)
                || leitor.getEmail().toLowerCase().contains(termo))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Leitor buscar(Long id) {
        return leitorRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Leitor não encontrado."));
    }

    public Leitor salvar(Long id, LeitorForm form) {
        String cpf = form.getCpf().replaceAll("\\D", "");
        if (!cpfValido(cpf)) {
            throw new RegraNegocioException("CPF inválido.");
        }
        String email = form.getEmail().trim().toLowerCase();
        leitorRepository.findByCpf(formatarCpf(cpf)).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                throw new RegraNegocioException("CPF já cadastrado.");
            }
        });
        leitorRepository.findByEmailIgnoreCase(email).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                throw new RegraNegocioException("E-mail já cadastrado.");
            }
        });
        Leitor leitor = id == null ? new Leitor() : buscar(id);
        leitor.setNome(form.getNome().trim());
        leitor.setCpf(formatarCpf(cpf));
        leitor.setEmail(email);
        leitor.setTelefone(form.getTelefone().trim());
        return leitorRepository.save(leitor);
    }

    public void excluir(Long id) {
        Leitor leitor = buscar(id);
        if (emprestimoRepository.existsByLeitorId(id)) {
            throw new RegraNegocioException("Não é possível excluir um leitor com histórico de empréstimos.");
        }
        leitorRepository.delete(leitor);
    }

    private String formatarCpf(String cpf) {
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);
    }

    private boolean cpfValido(String cpf) {
        if (cpf.length() != 11 || cpf.chars().distinct().count() == 1) {
            return false;
        }
        for (int digito = 9; digito <= 10; digito++) {
            int soma = 0;
            for (int i = 0; i < digito; i++) {
                soma += Character.digit(cpf.charAt(i), 10) * (digito + 1 - i);
            }
            int calculado = (soma * 10) % 11;
            if (calculado == 10) {
                calculado = 0;
            }
            if (calculado != Character.digit(cpf.charAt(digito), 10)) {
                return false;
            }
        }
        return true;
    }
}
