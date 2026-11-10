package br.com.biblioteca;

import br.com.biblioteca.dto.AutorForm;
import br.com.biblioteca.dto.EmprestimoForm;
import br.com.biblioteca.dto.LeitorForm;
import br.com.biblioteca.dto.LivroForm;
import br.com.biblioteca.model.Autor;
import br.com.biblioteca.model.Leitor;
import br.com.biblioteca.model.Livro;
import br.com.biblioteca.model.StatusEmprestimo;
import br.com.biblioteca.service.AutorService;
import br.com.biblioteca.service.EmprestimoService;
import br.com.biblioteca.service.LeitorService;
import br.com.biblioteca.service.LivroService;
import br.com.biblioteca.service.RegraNegocioException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BibliotecaApplicationTests {
    @Autowired private MockMvc mvc;
    @Autowired private AutorService autorService;
    @Autowired private LivroService livroService;
    @Autowired private LeitorService leitorService;
    @Autowired private EmprestimoService emprestimoService;

    @Test
    void paginasPrincipaisCarregam() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("index"));
        mvc.perform(get("/autores")).andExpect(status().isOk());
        mvc.perform(get("/livros")).andExpect(status().isOk());
        mvc.perform(get("/leitores")).andExpect(status().isOk());
        mvc.perform(get("/emprestimos")).andExpect(status().isOk());
    }

    @Test
    void impedeDoisEmprestimosDoMesmoLivroEAceitaDevolucao() {
        AutorForm autorForm = new AutorForm();
        autorForm.setNome("Machado de Assis");
        autorForm.setNacionalidade("Brasileira");
        Autor autor = autorService.salvar(null, autorForm);

        LivroForm livroForm = new LivroForm();
        livroForm.setTitulo("Dom Casmurro");
        livroForm.setIsbn("9780000000010");
        livroForm.setAnoPublicacao(1899);
        livroForm.setAutorId(autor.getId());
        Livro livro = livroService.salvar(null, livroForm);

        LeitorForm leitorForm = new LeitorForm();
        leitorForm.setNome("Leitor Teste");
        leitorForm.setCpf("52998224725");
        leitorForm.setEmail("leitor-teste@example.com");
        leitorForm.setTelefone("(47) 99999-9999");
        Leitor leitor = leitorService.salvar(null, leitorForm);

        EmprestimoForm form = new EmprestimoForm();
        form.setLeitorId(leitor.getId());
        form.setLivroId(livro.getId());
        form.setDataEmprestimo(LocalDate.now());
        form.setDataDevolucaoPrevista(LocalDate.now().plusDays(14));

        var emprestimo = emprestimoService.registrar(form);
        assertEquals(StatusEmprestimo.ATIVO, emprestimo.getStatus());
        assertThrows(RegraNegocioException.class, () -> emprestimoService.registrar(form));
        assertTrue(emprestimoService.livrosDisponiveis().stream().noneMatch(l -> l.getId().equals(livro.getId())));

        emprestimoService.devolver(emprestimo.getId(), LocalDate.now());
        assertEquals(StatusEmprestimo.DEVOLVIDO, emprestimoService.buscar(emprestimo.getId()).getStatus());
        assertTrue(emprestimoService.livrosDisponiveis().stream().anyMatch(l -> l.getId().equals(livro.getId())));
    }

    @Test
    void recusaCpfInvalido() {
        LeitorForm form = new LeitorForm();
        form.setNome("Pessoa Teste");
        form.setCpf("111.111.111-11");
        form.setEmail("teste-invalido@example.com");
        form.setTelefone("47999999999");
        assertThrows(RegraNegocioException.class, () -> leitorService.salvar(null, form));
    }
}
