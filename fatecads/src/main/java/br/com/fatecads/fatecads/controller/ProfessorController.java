package br.com.fatecads.fatecads.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.fatecads.fatecads.entity.Professor;
import br.com.fatecads.fatecads.service.ProfessorService;



@Controller
@RequestMapping("/professores")
public class ProfessorController {
    
    // Injeção de dependêccia da service de alunos
    @Autowired
    private ProfessorService professorService;

    // Método para salvar um aluno
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Professor professor) {
        professorService.save(professor);
        return "redirect:/professores/listar";
    }   

    // Método para listar todos os alunos
    @GetMapping("/listar")
    public String listar(Model model) {
        
        model.addAttribute("professores", professorService.findAll());
        return "professor/listarProfessor";
    }

    // Método para criar um novo aluno e abrir um novo formulário
    @GetMapping("/criar")
    public String criarForm(Model model) {
        model.addAttribute("professor", new Professor());
        return "professor/formularioProfessor";
    }

    // Método para excluir um aluno pelo ID
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id) {
        professorService.deleteById(id);
        return "redirect:/professores/listar";
    }
    
    // Método para editar um aluno pelo ID
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model) {
        Professor professor = professorService.findById(id);
        model.addAttribute("professor", professor);
        return "professor/formularioProfessor";
    }
    
}
