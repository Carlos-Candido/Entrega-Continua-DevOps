package br.com.fatecads.fatecads.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.fatecads.fatecads.entity.ItemDoPedido;
import br.com.fatecads.fatecads.entity.Pedido;
import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.entity.Usuario;
import br.com.fatecads.fatecads.repository.UsuarioRepository;
import br.com.fatecads.fatecads.service.PedidoService;
import br.com.fatecads.fatecads.service.ProdutoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Área da loja, exclusiva do perfil CLIENTE.
 * O carrinho vive na sessão HTTP e a finalização registra um Pedido
 * (compra fake, sem pagamento) e mostra a tela de confirmação.
 */
@Controller
@RequestMapping("/loja")
public class LojaController {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private static final String SESSAO_CARRINHO = "carrinho";

    // Página inicial da loja: vitrine com os produtos
    @GetMapping
    public String loja(Model model, HttpServletRequest request, Authentication authentication) {
        List<Produto> produtos = produtoService.findAll();
        model.addAttribute("produtos", produtos);
        model.addAttribute("marcas", produtos.stream().map(Produto::getMarcaProduto).distinct().sorted().toList());
        popularLoja(model, request, authentication);
        return "loja/index";
    }

    // Catálogo com busca por descrição ou marca e filtro de marca
    @GetMapping("/produtos")
    public String produtos(@RequestParam(required = false) String q,
            @RequestParam(required = false) String marca,
            Model model, HttpServletRequest request, Authentication authentication) {
        List<Produto> produtos = produtoService.findAll();
        model.addAttribute("marcas", produtos.stream().map(Produto::getMarcaProduto).distinct().sorted().toList());
        if (q != null && !q.isBlank()) {
            String termo = q.trim().toLowerCase(Locale.ROOT);
            produtos = produtos.stream()
                    .filter(p -> p.getDescricaoProduto().toLowerCase(Locale.ROOT).contains(termo)
                            || p.getMarcaProduto().toLowerCase(Locale.ROOT).contains(termo))
                    .toList();
        }
        if (marca != null && !marca.isBlank()) {
            String marcaFiltrada = marca.trim();
            produtos = produtos.stream()
                    .filter(p -> p.getMarcaProduto().equalsIgnoreCase(marcaFiltrada))
                    .toList();
        }
        model.addAttribute("termo", q);
        model.addAttribute("marcaSelecionada", marca);
        model.addAttribute("produtos", produtos);
        popularLoja(model, request, authentication);
        return "loja/produtos";
    }

    // Detalhe do produto selecionado
    @GetMapping("/produto/{id}")
    public String produto(@PathVariable Integer id, Model model,
            HttpServletRequest request, Authentication authentication) {
        popularLoja(model, request, authentication);
        Produto produto = produtoService.findById(id);
        if (produto == null) {
            return "redirect:/loja/produtos";
        }
        model.addAttribute("produto", produto);
        return "loja/produto";
    }

    // Página do carrinho
    @GetMapping("/carrinho")
    public String carrinho(Model model, HttpServletRequest request, Authentication authentication) {
        popularLoja(model, request, authentication);
        model.addAttribute("itens", model.getAttribute("itensCarrinho"));
        model.addAttribute("subtotal", model.getAttribute("totalPedido"));
        model.addAttribute("total", model.getAttribute("totalPedido"));
        return "loja/carrinho";
    }

    // Adicionar produto ao carrinho
    @PostMapping("/carrinho/adicionar/{id}")
    public String adicionar(@PathVariable Integer id,
            @RequestParam(defaultValue = "1") Integer quantidade,
            @RequestParam(defaultValue = "/loja/produtos") String retorno,
            HttpServletRequest request) {

        Produto produto = produtoService.findById(id);
        if (produto == null) {
            return "redirect:/loja/produtos";
        }

        List<ItemDoPedido> carrinho = obterCarrinho(request);

        // Se o produto já está no carrinho, apenas soma a quantidade
        boolean produtoNoCarrinho = false;
        for (ItemDoPedido item : carrinho) {
            if (item.getProduto().getIdProduto().equals(id)) {
                item.setQuantidade(item.getQuantidade() + quantidade);
                item.atualizarSubtotal();
                produtoNoCarrinho = true;
                break;
            }
        }

        if (!produtoNoCarrinho) {
            ItemDoPedido item = new ItemDoPedido();
            item.setProduto(produto);
            item.setPreco(produto.getValorProduto());
            item.setQuantidade(quantidade);
            item.atualizarSubtotal();
            carrinho.add(item);
        }

        return "redirect:" + retorno + (retorno.contains("?") ? "&" : "?") + "added=1";
    }

    // Alterar quantidade de um item do carrinho
    @PostMapping("/carrinho/alterar/{id}")
    public String alterar(@PathVariable Integer id, @RequestParam(required = false) Integer quantidade,
            HttpServletRequest request) {

        List<ItemDoPedido> carrinho = obterCarrinho(request);
        for (ItemDoPedido item : carrinho) {
            if (item.getProduto().getIdProduto().equals(id)) {
                // Quantidade zerada ou inválida remove o item
                if (quantidade == null || quantidade <= 0) {
                    carrinho.remove(item);
                } else {
                    item.setQuantidade(quantidade);
                    item.atualizarSubtotal();
                }
                break;
            }
        }
        return "redirect:/loja/carrinho";
    }

    // Remover item do carrinho
    @PostMapping("/carrinho/remover/{id}")
    public String remover(@PathVariable Integer id, HttpServletRequest request) {
        List<ItemDoPedido> carrinho = obterCarrinho(request);
        carrinho.removeIf(item -> item.getProduto().getIdProduto().equals(id));
        return "redirect:/loja/carrinho";
    }

    // Finalizar compra: registra o pedido (compra fake, sem pagamento) e limpa o carrinho
    @PostMapping("/finalizar")
    public String finalizar(Model model, Authentication authentication, HttpServletRequest request) {
        List<ItemDoPedido> carrinho = obterCarrinho(request);
        if (carrinho.isEmpty()) {
            return "redirect:/loja/carrinho";
        }

        Pedido pedido = new Pedido();
        List<ItemDoPedido> itens = new ArrayList<>();
        for (ItemDoPedido itemCarrinho : carrinho) {
            ItemDoPedido item = new ItemDoPedido();
            item.setProduto(itemCarrinho.getProduto());
            item.setPreco(itemCarrinho.getPreco());
            item.setQuantidade(itemCarrinho.getQuantidade());
            item.atualizarSubtotal();
            itens.add(item);
        }
        pedido.setItens(itens);

        // Vincula o pedido ao usuário CLIENTE logado
        Usuario cliente = usuarioRepository.findByLoginUsuario(authentication.getName()).orElse(null);
        pedido.setCliente(cliente);

        pedido = pedidoService.salvarPedido(pedido);

        // Limpa o carrinho após finalizar a compra
        request.getSession().setAttribute(SESSAO_CARRINHO, new ArrayList<ItemDoPedido>());

        model.addAttribute("pedido", pedido);
        model.addAttribute("itens", pedido.getItens());
        model.addAttribute("total", pedido.getTotalPedido());
        popularLoja(model, request, authentication);
        return "loja/confirmacao";
    }

    // Dados compartilhados pelo header e pelo carrinho lateral
    private void popularLoja(Model model, HttpServletRequest request, Authentication authentication) {
        if (authentication != null) {
            model.addAttribute("clienteNome", usuarioRepository.findByLoginUsuario(authentication.getName())
                    .map(Usuario::getNomeUsuario).orElse(authentication.getName()));
        }
        List<ItemDoPedido> carrinho = obterCarrinho(request);
        model.addAttribute("itensCarrinho", carrinho);
        model.addAttribute("totalCarrinho", contarItens(carrinho));
        model.addAttribute("totalPedido", calcularTotal(carrinho));
    }

    // Obtém o carrinho da sessão, criando-o quando ainda não existe
    @SuppressWarnings("unchecked")
    private List<ItemDoPedido> obterCarrinho(HttpServletRequest request) {
        HttpSession session = request.getSession();
        List<ItemDoPedido> carrinho = (List<ItemDoPedido>) session.getAttribute(SESSAO_CARRINHO);
        if (carrinho == null) {
            carrinho = new ArrayList<>();
            session.setAttribute(SESSAO_CARRINHO, carrinho);
        }
        return carrinho;
    }

    private int contarItens(List<ItemDoPedido> carrinho) {
        return carrinho.stream().mapToInt(ItemDoPedido::getQuantidade).sum();
    }

    private Double calcularTotal(List<ItemDoPedido> carrinho) {
        return carrinho.stream().mapToDouble(ItemDoPedido::getSubtotal).sum();
    }

}
