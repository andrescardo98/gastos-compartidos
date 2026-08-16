package com.gastoscompartidos.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad JPA de liquidacion. Vive en infrastructure, no en domain.
 *
 * <p>Objeto anemico, como el resto de entidades JPA: solo mapea filas. La logica esta en
 * {@code domain.model.Liquidacion} y el {@code LiquidacionMapper} traduce entre ambos.
 *
 * <p>El {@code Dinero} del dominio se aplana en dos columnas ({@code importe} +
 * {@code codigo_moneda}), igual que en {@code GastoJpaEntity}. Grupo, pagador y receptor
 * se referencian por UUID plano y no con {@code @ManyToOne}: son aggregates distintos y
 * cargarlos en cascada seria arrastrar medio modelo por leer un pago.
 */
@Entity
@Table(name = "liquidaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LiquidacionJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "grupo_id", nullable = false)
    private UUID grupoId;

    @Column(name = "pagador_id", nullable = false)
    private UUID pagadorId;

    @Column(name = "receptor_id", nullable = false)
    private UUID receptorId;

    /** precision/scale explicitos: NUMERIC(19,2), nunca un tipo de coma flotante. */
    @Column(name = "importe", nullable = false, precision = 19, scale = 2)
    private BigDecimal importe;

    @Column(name = "codigo_moneda", nullable = false, length = 3)
    private String codigoMoneda;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;
}
