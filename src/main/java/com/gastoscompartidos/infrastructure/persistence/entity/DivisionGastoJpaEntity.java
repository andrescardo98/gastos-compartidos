package com.gastoscompartidos.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entidad JPA de la parte que le toca a un usuario en un gasto.
 *
 * <p>En el dominio {@code DivisionGasto} es un value object sin identidad; aqui necesita
 * una PK tecnica porque las tablas relacionales la exigen. Esa PK no cruza al dominio:
 * el mapper la descarta.
 */
@Entity
@Table(name = "divisiones_gasto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DivisionGastoJpaEntity {

    /** PK tecnica, invisible para el dominio. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "importe", nullable = false, precision = 19, scale = 2)
    private BigDecimal importe;

    @Column(name = "codigo_moneda", nullable = false, length = 3)
    private String codigoMoneda;
}
