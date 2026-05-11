package br.com.fatecads.fatecads.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.fatecads.fatecads.entity.Curso;
import br.com.fatecads.fatecads.service.CursoService;
import br.com.fatecads.fatecads.service.DisciplinaService;

@Controller
@RequestMapping("/curso")
public class CursoController {

 //Injeção de dependência da service de alunos
    @Autowired
    private CursoService cursoService;

    @Autowired
    private DisciplinaService disciplinaService;

    //Método para salvar um curso
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Curso curso){
        cursoService.save(curso);
        return "redirect:/curso/listar";
    }

    @GetMapping("/listar")
    public String listar(Model model){
        model.addAttribute("cursos", cursoService.findAll());
        return "curso/listarCursos";
    }

    //Método para criar um novo aluno e abrir o formulário
    @GetMapping("/criar")
    public String criarForm(Model model) {
        model.addAttribute("curso", new Curso());
        model.addAttribute("disciplinas", disciplinaService.findAll());
        return "curso/formularioCurso";
    }

    //Método para excluir uma aluno por ID
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id) {
        cursoService.deleteById(id);
        return "redirect:/curso/listar";
    }
    
    //Método para editar um aluno pelo ID
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model) {
        Curso curso = cursoService.findById(id);
        model.addAttribute("curso", curso);
        model.addAttribute("disciplinas", disciplinaService.findAll());
        return "curso/formularioCurso";
    }
}
