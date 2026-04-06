package br.com.fatecads.fatecads.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.fatecads.fatecads.entity.Aluno;
import br.com.fatecads.fatecads.service.AlunoService;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/alunos")
public class AlunoController {

    //Injeção de dependência da service de alunos
    @Autowired
    private AlunoService alunoService;

    //Método para salvar um aluno
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Aluno aluno){
        alunoService.save(aluno);
        return "redirect:/alunos/listar";
    }

    @GetMapping("/listar")
    public String listar(Model model){
        model.addAttribute("alunos", alunoService.findAll());
        return "aluno/listarAlunos";
    }

    //Método para criar um novo aluno e abrir o formulário
    @GetMapping("/criar")
    public String criarForm(Model model) {
        model.addAttribute("aluno", new Aluno());
        return "aluno/formularioAluno";
    }
    
}
