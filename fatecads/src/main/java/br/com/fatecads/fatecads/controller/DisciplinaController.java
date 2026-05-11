package br.com.fatecads.fatecads.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.fatecads.fatecads.entity.Disciplina;
import br.com.fatecads.fatecads.service.DisciplinaService;
import br.com.fatecads.fatecads.service.ProfessorService;

@Controller
@RequestMapping("/disciplina")
public class DisciplinaController {
    //Injeção de dependência da service de disciplinas
    @Autowired
        private DisciplinaService disciplinaService;

    @Autowired
        private ProfessorService professorService;

    //Método para salvar uma disciplina
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Disciplina disciplina){
        disciplinaService.save(disciplina);
        return "redirect:/disciplina/listar";
    }

    @GetMapping("/listar")
    public String listar(Model model){
        model.addAttribute("disciplinas", disciplinaService.findAll());
        return "disciplina/listarDisciplinas";
    }

    //Método para criar um novo professor e abrir o formulário
    @GetMapping("/criar")
    public String criarForm(Model model) {
        model.addAttribute("disciplina", new Disciplina());
        model.addAttribute("professores", professorService.findAll());
        return "disciplina/formularioDisciplina";
    }

    //Método para excluir uma disciplina por ID
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id) {
        disciplinaService.deleteById(id);
        return "redirect:/disciplina/listar";
    }
    
    //Método para editar uma disciplina pelo ID
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model) {
        Disciplina disciplina = disciplinaService.findById(id);
        model.addAttribute("disciplina", disciplina);
        model.addAttribute("professores", professorService.findAll());
        return "disciplina/formularioDisciplina";
    }
}
