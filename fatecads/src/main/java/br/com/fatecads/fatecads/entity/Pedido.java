package br.com.fatecads.fatecads.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idPedido;

    private LocalDate dataPedido;

    @ManyToOne
    @JoinColumn(name = "id_cliente_fk")
    private Usuario cliente;

    private Double totalPedido;

    @ManyToOne
    @JoinColumn(name = "idAluno_fk")
    private Aluno aluno;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<ItemDoPedido> itens;

    // Médoto para calcular o total
    public Double calcularTotal(){
        Double total = 0.0;

        if (itens != null) {
            for (ItemDoPedido item : itens) {
                total += item.getSubtotal();
            }
        }
        return total;
    }

    public void atualizarTotal() {
        this.totalPedido = calcularTotal();
    }

}
