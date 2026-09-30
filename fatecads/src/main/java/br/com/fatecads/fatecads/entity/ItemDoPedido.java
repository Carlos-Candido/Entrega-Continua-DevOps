package br.com.fatecads.fatecads.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ItemDoPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idItem;

    private Integer quantidade;
    
    private Double preco;

    private Double subtotal;

    @ManyToOne
    @JoinColumn(name = "idPedido_fk")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "idProduto_fk")
    private Produto produto;

    // Método para calcular o subtotal
    public Double calcularSubtotal() {
        return quantidade * preco;
    }

    public void atualizarSubtotal(){
        this.subtotal = calcularSubtotal();
    }
}
