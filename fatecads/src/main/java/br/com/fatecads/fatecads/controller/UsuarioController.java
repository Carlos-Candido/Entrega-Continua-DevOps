package br.com.fatecads.fatecads.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.fatecads.fatecads.entity.Usuario;
import br.com.fatecads.fatecads.service.UsuarioService;



@Controller
@RequestMapping("/usuarios")
public class UsuarioController {
    
    // Injeção de dependêccia da service de alunos
    @Autowired
    private UsuarioService usuarioService;

    // Método para salvar um aluno
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Usuario usuario) {
        usuarioService.save(usuario);
        return "/login";
    }   

    // Método para listar todos os alunos
    @GetMapping("/listar")
    public String listar(Model model) {
        
        model.addAttribute("usuarios", usuarioService.findAll());
        return "usuario/listarUsuario";
    }

    // Método para criar um novo aluno e abrir um novo formulário
    @GetMapping("/criar")
    public String criarForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuario/formularioUsuario";
    }

    // Cadastro público: o visitante escolhe o perfil da conta
    @PostMapping("/cadastro/salvar")
    public String salvarCadastro(@ModelAttribute Usuario usuario) {
        if (usuario.getRole() == null || usuario.getRole().isBlank()) {
            usuario.setRole("ROLE_CLIENTE");
        }
        usuarioService.save(usuario);
        return "redirect:/login";
    }

    @GetMapping("/cadastro")
    public String cadastroForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuario/cadastroUsuario";
    }


    // Método para excluir um aluno pelo ID
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id) {
        usuarioService.deleteById(id);
        return "redirect:/usuarios/listar";
    }
    
    // Método para editar um aluno pelo ID
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model) {
        Usuario usuario = usuarioService.findById(id);
        model.addAttribute("usuario", usuario);
        return "usuario/formularioUsuario";
    }
    
}
