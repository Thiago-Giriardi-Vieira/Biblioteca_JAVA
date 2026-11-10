package br.com.biblioteca.controller;

import br.com.biblioteca.dto.LeitorForm;
import br.com.biblioteca.service.LeitorService;
import br.com.biblioteca.service.RegraNegocioException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/leitores")
public class LeitorController {
    private final LeitorService leitorService;

    public LeitorController(LeitorService leitorService) {
        this.leitorService = leitorService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String busca, @RequestParam(required = false) String ordem, Model model) {
        model.addAttribute("leitores", leitorService.listar(busca, ordem));
        model.addAttribute("busca", busca);
        model.addAttribute("ordem", ordem);
        return "leitores/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("form", new LeitorForm());
        model.addAttribute("id", null);
        return "leitores/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", LeitorForm.de(leitorService.buscar(id)));
        model.addAttribute("id", id);
        return "leitores/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@RequestParam(required = false) Long id, @Valid @ModelAttribute("form") LeitorForm form,
                         BindingResult resultado, Model model, RedirectAttributes redirect) {
        if (!resultado.hasErrors()) {
            try {
                leitorService.salvar(id, form);
                redirect.addFlashAttribute("sucesso", "Leitor salvo com sucesso.");
                return "redirect:/leitores";
            } catch (RegraNegocioException erro) {
                resultado.reject("regra", erro.getMessage());
            }
        }
        model.addAttribute("id", id);
        return "leitores/formulario";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            leitorService.excluir(id);
            redirect.addFlashAttribute("sucesso", "Leitor excluído.");
        } catch (RegraNegocioException erro) {
            redirect.addFlashAttribute("erro", erro.getMessage());
        }
        return "redirect:/leitores";
    }
}
