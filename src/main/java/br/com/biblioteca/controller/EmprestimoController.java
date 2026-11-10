package br.com.biblioteca.controller;

import br.com.biblioteca.dto.EmprestimoForm;
import br.com.biblioteca.model.Emprestimo;
import br.com.biblioteca.service.EmprestimoService;
import br.com.biblioteca.service.LeitorService;
import br.com.biblioteca.service.RegraNegocioException;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;

@Controller
@RequestMapping("/emprestimos")
public class EmprestimoController {
    private final EmprestimoService emprestimoService;
    private final LeitorService leitorService;

    public EmprestimoController(EmprestimoService emprestimoService, LeitorService leitorService) {
        this.emprestimoService = emprestimoService;
        this.leitorService = leitorService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String busca, @RequestParam(required = false) String status, Model model) {
        model.addAttribute("emprestimos", emprestimoService.listar(busca, status));
        model.addAttribute("busca", busca);
        model.addAttribute("statusSelecionado", status);
        return "emprestimos/lista";
    }

    private void prepararFormulario(Model model) {
        model.addAttribute("leitores", leitorService.listar(null, null));
        model.addAttribute("livros", emprestimoService.livrosDisponiveis());
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("form", new EmprestimoForm());
        prepararFormulario(model);
        return "emprestimos/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("form") EmprestimoForm form,
                         BindingResult resultado, Model model, RedirectAttributes redirect) {
        if (!resultado.hasErrors()) {
            try {
                emprestimoService.registrar(form);
                redirect.addFlashAttribute("sucesso", "Empréstimo registrado com sucesso.");
                return "redirect:/emprestimos";
            } catch (RegraNegocioException erro) {
                resultado.reject("regra", erro.getMessage());
            }
        }
        prepararFormulario(model);
        return "emprestimos/formulario";
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        model.addAttribute("emprestimo", emprestimoService.buscar(id));
        return "emprestimos/detalhe";
    }

    @GetMapping("/{id}/devolucao")
    public String devolucao(@PathVariable Long id, Model model) {
        Emprestimo emprestimo = emprestimoService.buscar(id);
        model.addAttribute("emprestimo", emprestimo);
        model.addAttribute("dataDevolucao", LocalDate.now());
        model.addAttribute("diasAtraso", emprestimoService.diasAtraso(emprestimo, LocalDate.now()));
        return "emprestimos/devolucao";
    }

    @PostMapping("/{id}/devolucao")
    public String confirmarDevolucao(@PathVariable Long id,
                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataDevolucao,
                                    RedirectAttributes redirect) {
        try {
            emprestimoService.devolver(id, dataDevolucao);
            redirect.addFlashAttribute("sucesso", "Devolução registrada com sucesso.");
            return "redirect:/emprestimos";
        } catch (RegraNegocioException erro) {
            redirect.addFlashAttribute("erro", erro.getMessage());
            return "redirect:/emprestimos/" + id + "/devolucao";
        }
    }
}
