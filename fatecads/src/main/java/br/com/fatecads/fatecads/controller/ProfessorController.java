package br.com.fatecads.fatecads.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.fatecads.fatecads.entity.Professor;
import br.com.fatecads.fatecads.service.ProfessorService;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/professor")
public class ProfessorController {
    //Injeção de dependência da service de professores
    @Autowired
        private ProfessorService professorService;

    //Método para salvar um professor
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Professor professor){
        professorService.save(professor);
        return "redirect:/professor/listar";
    }

    @GetMapping("/listar")
    public String listar(Model model){
        model.addAttribute("professores", professorService.findAll());
        return "professor/listarProfessores";
    }

    //Método para criar um novo professor e abrir o formulário
    @GetMapping("/criar")
    public String criarForm(Model model) {
        model.addAttribute("professor", new Professor());
        return "professor/formularioProfessor";
    }

    //Método para excluir um professor por ID
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id) {
        professorService.deleteById(id);
        return "redirect:/professor/listar";
    }
    
    //Método para editar um professor pelo ID
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model) {
        Professor professor = professorService.findById(id);
        model.addAttribute("professor", professor);
        return "professor/formularioProfessor";
    }
}
