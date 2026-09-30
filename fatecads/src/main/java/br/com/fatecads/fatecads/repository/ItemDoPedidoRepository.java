package br.com.fatecads.fatecads.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fatecads.fatecads.entity.ItemDoPedido;

public interface ItemDoPedidoRepository extends JpaRepository<ItemDoPedido, Integer> {
    
}
