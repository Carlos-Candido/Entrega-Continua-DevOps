package br.com.fatecads.fatecads.entity;

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
public class Professor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idProfessor;

    @Column(nullable = false, length = 40)
    private String nomeProfessor;

    @Column(nullable = false, length = 40)
    private String telefoneProfessor;

    @Column(nullable = false, length = 40)
    private String graduacaoProfessor;

    @Column(nullable = false, length = 40)
    private String rmProfessor;
}
