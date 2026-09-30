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
import br.com.fatecads.fatecads.service.CursoService;
import br.com.fatecads.fatecads.service.DisciplinaService;
import br.com.fatecads.fatecads.service.ProfessorService;




@Controller
@RequestMapping("/disciplinas")
public class DisciplinaController {
    
    // Injeção de dependêccia da service de alunos
    @Autowired
    private DisciplinaService disciplinaService;

    @Autowired
    private CursoService cursoService;

    @Autowired
    private ProfessorService professorService;

    // Método para salvar um aluno
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Disciplina disciplina) {
        disciplinaService.save(disciplina);
        return "redirect:/disciplinas/listar";
    }   

    // Método para listar todos os alunos
    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("disciplinas", disciplinaService.findAll());
        return "disciplina/listarDisciplina";
    }

    // Método para criar um novo aluno e abrir um novo formulário
    @GetMapping("/criar")
    public String criarForm(Model model) {
        model.addAttribute("disciplina", new Disciplina());
        model.addAttribute("cursos", cursoService.findAll());
        model.addAttribute("professores", professorService.findAll());
        return "disciplina/formularioDisciplina";
    }

    // Método para excluir um aluno pelo ID
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id) {
        disciplinaService.deleteById(id);
        return "redirect:/disciplinas/listar";
    }
    
    // Método para editar um aluno pelo ID
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model) {
        Disciplina disciplina = disciplinaService.findById(id);
        model.addAttribute("cursos", cursoService.findAll());
        model.addAttribute("professores", professorService.findAll());
        model.addAttribute("disciplina", disciplina);
        return "disciplina/formularioDisciplina";
    }
    
}
