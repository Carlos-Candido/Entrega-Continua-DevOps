package br.com.fatecads.fatecads.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fatecads.fatecads.entity.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    
}
