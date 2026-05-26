package br.com.fatecads.fatecads.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.fatecads.fatecads.exception.InvalidPasswordResetTokenException;
import br.com.fatecads.fatecads.service.PasswordResetService;

@Controller
public class PasswordResetController {
    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/recuperar-senha")
    public String showPasswordResetRequestForm() {
        return "auth/recuperarSenha";
    }

    @PostMapping("/recuperar-senha")
    public String requestPasswordReset(
            @RequestParam String email,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        if (!StringUtils.hasText(email)) {
            redirectAttributes.addFlashAttribute("erro", "Informe o email cadastrado.");
            return "redirect:/recuperar-senha";
        }

        String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(null)
                .replaceQuery(null)
                .build()
                .toUriString();

        passwordResetService.requestPasswordReset(email, baseUrl);
        redirectAttributes.addFlashAttribute(
                "sucesso",
                "Se o email estiver cadastrado, enviaremos um link para redefinir sua senha.");

        return "redirect:/recuperar-senha";
    }

    @GetMapping("/redefinir-senha")
    public String showPasswordResetForm(
            @RequestParam String token,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (!passwordResetService.isTokenValid(token)) {
            redirectAttributes.addFlashAttribute("erro", "Token invalido ou expirado. Solicite um novo link.");
            return "redirect:/recuperar-senha";
        }

        model.addAttribute("token", token);
        return "auth/redefinirSenha";
    }

    @PostMapping("/redefinir-senha")
    public String resetPassword(
            @RequestParam String token,
            @RequestParam String senha,
            @RequestParam String confirmacaoSenha,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (!StringUtils.hasText(senha) || senha.length() < 8) {
            model.addAttribute("token", token);
            model.addAttribute("erro", "A senha deve ter pelo menos 8 caracteres.");
            return "auth/redefinirSenha";
        }

        if (!senha.equals(confirmacaoSenha)) {
            model.addAttribute("token", token);
            model.addAttribute("erro", "A confirmacao de senha nao confere.");
            return "auth/redefinirSenha";
        }

        try {
            passwordResetService.resetPassword(token, senha);
        } catch (InvalidPasswordResetTokenException ex) {
            redirectAttributes.addFlashAttribute("erro", "Token invalido ou expirado. Solicite um novo link.");
            return "redirect:/recuperar-senha";
        }

        redirectAttributes.addFlashAttribute("sucesso", "Senha redefinida com sucesso. Entre com a nova senha.");
        return "redirect:/login";
    }
}
