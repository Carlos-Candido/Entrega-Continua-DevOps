package br.com.fatecads.fatecads.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idUsuario;

    @Column(nullable = false, length = 80)
    private String nomeUsuario;

    @Column(nullable = false, length = 80)
    private String emailUsuario;

    @Column(nullable = false, length = 50)
    private String loginUsuario;

    @Column(nullable = false, length = 150)
    private String senhaUsuario;

    @Column(length = 20)
    private String telefoneUsuario;

    @Column(length = 6)
    private String codigoRecuperacao;

    private LocalDateTime expiracaoCodigo;

    private String role = "ROLE_USER";
}