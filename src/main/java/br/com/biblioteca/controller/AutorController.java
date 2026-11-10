package br.com.biblioteca.controller;

import br.com.biblioteca.dto.AutorForm;
import br.com.biblioteca.model.Autor;
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

@Controller
@RequestMapping("/autores")
public class AutorController {
    private final AutorService autorService;
    private final LivroService livroService;

    public AutorController(AutorService autorService, LivroService livroService) {
        this.autorService = autorService;
        this.livroService = livroService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String busca, Model model) {
        model.addAttribute("autores", autorService.listar(busca));
        model.addAttribute("busca", busca);
        return "autores/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("form", new AutorForm());
        model.addAttribute("id", null);
        return "autores/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", AutorForm.de(autorService.buscar(id)));
        model.addAttribute("id", id);
        return "autores/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@RequestParam(required = false) Long id, @Valid @ModelAttribute("form") AutorForm form,
                         BindingResult resultado, Model model, RedirectAttributes redirect) {
        if (!resultado.hasErrors()) {
            try {
                autorService.salvar(id, form);
                redirect.addFlashAttribute("sucesso", "Autor salvo com sucesso.");
                return "redirect:/autores";
            } catch (RegraNegocioException erro) {
                resultado.reject("regra", erro.getMessage());
            }
        }
        model.addAttribute("id", id);
        return "autores/formulario";
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        model.addAttribute("autor", autorService.buscar(id));
        model.addAttribute("livros", livroService.listarPorAutor(id));
        return "autores/detalhe";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            autorService.excluir(id);
            redirect.addFlashAttribute("sucesso", "Autor excluído.");
        } catch (RegraNegocioException erro) {
            redirect.addFlashAttribute("erro", erro.getMessage());
        }
        return "redirect:/autores";
    }

    @GetMapping("/{id}/imagem")
    @ResponseBody
    public ResponseEntity<byte[]> imagem(@PathVariable Long id) {
        Autor autor = autorService.buscar(id);
        if (autor.getImagem() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().header(HttpHeaders.CACHE_CONTROL, "max-age=3600")
            .contentType(MediaType.parseMediaType(autor.getTipoImagem())).body(autor.getImagem());
    }
}
