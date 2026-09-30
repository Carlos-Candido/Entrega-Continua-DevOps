package br.com.fatecads.fatecads.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.repository.ProdutoRepository;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    // Método para salvar um produto
    public Produto save(Produto produto) {
        return produtoRepository.save(produto);
    }

    // Método para listar todos os produtos
    public List<Produto> findAll() {
        return produtoRepository.findAll();
    }

    // Método para buscar todos os produtos
    public Produto findById(Integer id) {
        return produtoRepository.findById(id).orElse(null);
    }

    public void deleteById(Integer id) {
        produtoRepository.deleteById(id);
    }
}
