package com.gastoscompartidos.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Entidad JPA de usuario. Vive en infrastructure, no en domain.
 *
 * <p>Es intencionadamente un objeto anemico (getters/setters, sin logica): su unico
 * trabajo es mapear filas. Toda la logica esta en {@code domain.model.Usuario}, y el
 * {@code UsuarioMapper} traduce entre ambos. Duplicar los campos es el precio de que
 * el dominio no dependa de JPA, y lo que permite cambiar el esquema sin tocar reglas
 * de negocio.
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "email", nullable = false, unique = true, length = 180)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
}
