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
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuariosService;

    //Método para salvar um usuario
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Usuario usuario){
        usuariosService.save(usuario);
        return "redirect:/usuario/listar";
    }

    @GetMapping("/listar")
    public String listar(Model model){
        model.addAttribute("usuarios", usuariosService.findAll());
        return "usuario/listarUsuarios";
    }

    @GetMapping("/criar")
    public String criarForm(Model model){
        model.addAttribute("usuario", new Usuario());
        return "usuario/formularioUsuario";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id) {
        usuariosService.deleteById(id);
        return "redirect:/usuario/listar";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model) {
        Usuario usuario = usuariosService.findById(id);
        model.addAttribute("usuario", usuario);
        return "usuario/formularioUsuario";
    }
}
