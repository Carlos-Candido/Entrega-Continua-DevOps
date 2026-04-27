package br.com.fatecads.fatecads.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fatecads.fatecads.entity.Disciplina;

public interface DisciplinaRepository extends JpaRepository<Disciplina, Integer> {
    
}
