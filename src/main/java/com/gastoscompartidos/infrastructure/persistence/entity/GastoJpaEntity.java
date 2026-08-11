package com.gastoscompartidos.infrastructure.persistence.entity;

import com.gastoscompartidos.domain.model.TipoDivision;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidad JPA de gasto, con sus divisiones como hijas en cascada.
 *
 * <p>{@code tipoDivision} se guarda como STRING, nunca ORDINAL: con ORDINAL, reordenar
 * el enum corrompe silenciosamente los datos ya guardados.
 */
@Entity
@Table(name = "gastos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GastoJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "grupo_id", nullable = false)
    private UUID grupoId;

    @Column(name = "pagador_id", nullable = false)
    private UUID pagadorId;

    @Column(name = "descripcion", nullable = false, length = 255)
    private String descripcion;

    /** precision/scale explicitos: NUMERIC(19,2), nunca un tipo de coma flotante. */
    @Column(name = "importe_total", nullable = false, precision = 19, scale = 2)
    private BigDecimal importeTotal;

    @Column(name = "codigo_moneda", nullable = false, length = 3)
    private String codigoMoneda;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_division", nullable = false, length = 30)
    private TipoDivision tipoDivision;

    /**
     * orphanRemoval: las divisiones no existen fuera de su gasto (son parte del agregado).
     * El fetch es LAZY; el repositorio usa un @EntityGraph para traerlas sin caer en N+1.
     */
    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "gasto_id", nullable = false)
    private List<DivisionGastoJpaEntity> divisiones = new ArrayList<>();

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;
}
