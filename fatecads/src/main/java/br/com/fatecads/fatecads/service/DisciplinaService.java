package br.com.fatecads.fatecads.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.fatecads.fatecads.entity.Disciplina;
import br.com.fatecads.fatecads.repository.DisciplinaRepository;

@Service
public class DisciplinaService {
    @Autowired
    private DisciplinaRepository disciplinaRepository;

    public Disciplina save(Disciplina disciplina) {
        return disciplinaRepository.save(disciplina);
    }

    public List<Disciplina> findAll() {
        return disciplinaRepository.findAll();
    }

    public void deleteById(Integer id) {
        disciplinaRepository.deleteById(id);
    }

    public Disciplina findById(Integer id) {
        return disciplinaRepository.findById(id).orElse(null);
    }
}
