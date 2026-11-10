package br.com.biblioteca.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.NoSuchElementException;

@ControllerAdvice
public class ErroControllerAdvice {
    @ExceptionHandler(NoSuchElementException.class)
    public String naoEncontrado(NoSuchElementException erro, Model model, HttpServletResponse resposta) {
        resposta.setStatus(HttpServletResponse.SC_NOT_FOUND);
        model.addAttribute("mensagem", erro.getMessage());
        return "erro";
    }
}
