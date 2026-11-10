package br.com.biblioteca.controller;

import br.com.biblioteca.dto.LivroForm;
import br.com.biblioteca.model.Livro;
import br.com.biblioteca.service.AutorService;
import br.com.biblioteca.service.LivroService;
import br.com.biblioteca.service.RegraNegocioException;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/livros")
public class LivroController {
    private final LivroService livroService;
    private final AutorService autorService;

    public LivroController(LivroService livroService, AutorService autorService) {
        this.livroService = livroService;
        this.autorService = autorService;
    }

    private void prepararFormulario(Model model) {
        model.addAttribute("autores", autorService.listar(null));
        model.addAttribute("categorias", List.of("Romance", "Clássico", "Fantasia", "Suspense", "História", "Ciência", "Biografia", "Outros"));
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String busca, @RequestParam(required = false) String categoria, Model model) {
        model.addAttribute("livros", livroService.listar(busca, categoria));
        model.addAttribute("busca", busca);
        model.addAttribute("categoriaSelecionada", categoria);
        model.addAttribute("categorias", List.of("Romance", "Clássico", "Fantasia", "Suspense", "História", "Ciência", "Biografia", "Outros"));
        return "livros/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("form", new LivroForm());
        model.addAttribute("id", null);
        prepararFormulario(model);
        return "livros/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", LivroForm.de(livroService.buscar(id)));
        model.addAttribute("id", id);
        prepararFormulario(model);
        return "livros/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@RequestParam(required = false) Long id, @Valid @ModelAttribute("form") LivroForm form,
                         BindingResult resultado, Model model, RedirectAttributes redirect) {
        if (!resultado.hasErrors()) {
            try {
                livroService.salvar(id, form);
                redirect.addFlashAttribute("sucesso", "Livro salvo com sucesso.");
                return "redirect:/livros";
            } catch (RegraNegocioException erro) {
                resultado.reject("regra", erro.getMessage());
            }
        }
        model.addAttribute("id", id);
        prepararFormulario(model);
        return "livros/formulario";
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        model.addAttribute("livro", livroService.buscar(id));
        return "livros/detalhe";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            livroService.excluir(id);
            redirect.addFlashAttribute("sucesso", "Livro excluído.");
        } catch (RegraNegocioException erro) {
            redirect.addFlashAttribute("erro", erro.getMessage());
        }
        return "redirect:/livros";
    }

    @GetMapping("/{id}/imagem")
    @ResponseBody
    public ResponseEntity<byte[]> imagem(@PathVariable Long id) {
        Livro livro = livroService.buscar(id);
        if (livro.getImagem() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().header(HttpHeaders.CACHE_CONTROL, "max-age=3600")
            .contentType(MediaType.parseMediaType(livro.getTipoImagem())).body(livro.getImagem());
    }
}
