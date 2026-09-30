package br.com.fatecads.fatecads.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.fatecads.fatecads.entity.Aluno;
import br.com.fatecads.fatecads.service.AlunoService;
import br.com.fatecads.fatecads.service.CursoService;



@Controller
@RequestMapping("/alunos")
public class AlunoController {
    
    // Injeção de dependêccia da service de alunos
    @Autowired
    private AlunoService alunoService;

    @Autowired
    private CursoService cursoService;

    // Método para salvar um aluno
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Aluno aluno) {
        alunoService.save(aluno);
        return "redirect:/alunos/listar";
    }   

    // Método para listar todos os alunos
    @GetMapping("/listar")
    public String listar(Model model) {
        
        model.addAttribute("alunos", alunoService.findAll());
        return "aluno/listarAluno";
    }

    // Método para criar um novo aluno e abrir um novo formulário
    @GetMapping("/criar")
    public String criarForm(Model model) {
        model.addAttribute("aluno", new Aluno());
        model.addAttribute("cursos", cursoService.findAll());
        return "aluno/formularioAluno";
    }

    // Método para excluir um aluno pelo ID
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id) {
        alunoService.deleteById(id);
        return "redirect:/alunos/listar";
    }
    
    // Método para editar um aluno pelo ID
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model) {
        Aluno aluno = alunoService.findById(id);
        model.addAttribute("cursos", cursoService.findAll());
        model.addAttribute("aluno", aluno);
        return "aluno/formularioAluno";
    }
    
}
