package br.com.fatecads.fatecads.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import br.com.fatecads.fatecads.entity.Aluno;
import br.com.fatecads.fatecads.entity.Pedido;
import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.service.AlunoService;
import br.com.fatecads.fatecads.service.PedidoService;
import br.com.fatecads.fatecads.service.ProdutoService;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private AlunoService alunoService;

    @Autowired
    private ProdutoService produtoService;

    @PostMapping
    @ResponseBody
    public Pedido salvarPedido(@RequestBody Pedido pedido) {
        return pedidoService.salvarPedido(pedido);
    }

    // Método para abrir a tela de cadastro de pedidos
    @GetMapping("/criar")
    public String criarForm(Model model) {
        model.addAttribute("pedido", new Pedido());
        
        // Alunos
        List<Aluno> alunos = alunoService.findAll();
        model.addAttribute("alunos", alunos);

        // Produtos
        List<Produto> produtos = produtoService.findAll();
        model.addAttribute("produtos", produtos);
        return "pedido/formularioPedido";
    }
    
}
