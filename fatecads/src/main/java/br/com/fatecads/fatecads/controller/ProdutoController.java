package br.com.fatecads.fatecads.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.service.ProdutoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    // Método para listar todos os produtos
    @GetMapping("/listar")
    public String listar(Model model) {
        List<Produto> produtos = produtoService.findAll();
        model.addAttribute("produtos", produtos);
        return "produto/listarProdutos";
    }

    // Método para abrir o formulário de criação de produtos
    @GetMapping("/criar")
    public String criarForm(Model model) {
        model.addAttribute("produto", new Produto());
        return "produto/formularioProduto";
    }

    @PostMapping("/salvar")
    public String salvar(
            @ModelAttribute Produto produto,
            @RequestParam("imagem") MultipartFile imagem) throws IOException {

        if (!imagem.isEmpty()) {
            produto.setImagemProduto(imagem.getBytes());
            produto.setTipoImagem(imagem.getContentType());
        }

        produtoService.save(produto);

        return "redirect:/produtos/listar";
    }

    @GetMapping("/imagem/{id}")
    public ResponseEntity<byte[]> mostrarImagem(@PathVariable Integer id) {

        Produto produto = produtoService.findById(id);

        if (produto == null || produto.getImagemProduto() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(produto.getTipoImagem()))
                .body(produto.getImagemProduto());
    }

    // Método para abrir o formulário de edição de produtos
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model) {
        Produto produto = produtoService.findById(id);
        model.addAttribute("produto", produto);
        return "produto/formularioProduto";
    }

    // Método para excluir um produto pelo ID
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id) {
        produtoService.deleteById(id);
        return "redirect:/produtos/listar";
    }

}
