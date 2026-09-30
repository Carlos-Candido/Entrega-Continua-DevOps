package br.com.fatecads.fatecads.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.fatecads.fatecads.entity.Disciplina;
import br.com.fatecads.fatecads.repository.DisciplinaRepository;

@Service
public class DisciplinaService {
    
    // Injeção de Dependência do repositório de alunos
    @Autowired
    private DisciplinaRepository disciplinaRepository;

    // Método para salvar um aluno
    public Disciplina save(Disciplina disciplina) {
        return disciplinaRepository.save(disciplina);
    }

    // Método para listar todos os alunos
    public List<Disciplina> findAll(){
        return disciplinaRepository.findAll();
    }

    public void deleteById(Integer id){
        disciplinaRepository.deleteById(id);
    }

    public Disciplina findById(Integer id){
        return disciplinaRepository.findById(id).orElse(null);
    }
} 
