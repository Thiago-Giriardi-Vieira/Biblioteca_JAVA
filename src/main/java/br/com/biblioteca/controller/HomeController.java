package br.com.biblioteca.controller;

import br.com.biblioteca.model.StatusEmprestimo;
import br.com.biblioteca.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final LivroService livroService;
    private final AutorService autorService;
    private final LeitorService leitorService;
    private final EmprestimoService emprestimoService;

    public HomeController(LivroService livroService, AutorService autorService, LeitorService leitorService, EmprestimoService emprestimoService) {
        this.livroService = livroService;
        this.autorService = autorService;
        this.leitorService = leitorService;
        this.emprestimoService = emprestimoService;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("totalLivros", livroService.listar(null, null).size());
        model.addAttribute("totalAutores", autorService.listar(null).size());
        model.addAttribute("totalLeitores", leitorService.listar(null, null).size());
        model.addAttribute("emprestimosAtivos", emprestimoService.contar(StatusEmprestimo.ATIVO));
        model.addAttribute("emprestimosAtrasados", emprestimoService.contar(StatusEmprestimo.ATRASADO));
        return "index";
    }
}
