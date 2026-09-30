package br.com.fatecads.fatecads.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fatecads.fatecads.entity.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
    
}
