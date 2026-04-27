package br.com.fatecads.fatecads.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fatecads.fatecads.entity.Professor;

public interface ProfessorRepository extends JpaRepository<Professor, Integer> {
    
}
