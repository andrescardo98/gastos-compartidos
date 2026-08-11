package com.gastoscompartidos.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Entidad de dominio: un usuario de la aplicacion.
 *
 * <p>Clase pura: sin anotaciones de Spring, JPA, Jackson ni Lombok. La persistencia
 * la resuelve {@code infrastructure.persistence} mediante su propia entidad JPA + mapper.
 *
 * <p>Identidad por {@code id}, no por valor: dos usuarios con el mismo email pero
 * distinto id son entidades distintas.
 */
public class Usuario {

    private final UUID id;
    private String nombre;
    private String email;
    /** Hash de la contrasena. El dominio nunca ve la contrasena en claro. */
    private String passwordHash;

    public Usuario(UUID id, String nombre, String email, String passwordHash) {
        // TODO: validar invariantes y lanzar DomainException si no se cumplen:
        //       - id no nulo
        //       - nombre no vacio
        //       - email no vacio y con formato valido
        //       - passwordHash no vacio (y verificar que NO parece texto plano)
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    /**
     * TODO: factory para dar de alta un usuario nuevo (genera el id).
     *       El hash llega ya calculado desde infrastructure/security.
     */
    public static Usuario registrar(String nombre, String email, String passwordHash) {
        throw new UnsupportedOperationException("TODO: implementar alta de usuario");
    }

    /**
     * TODO: cambiar el nombre visible, revalidando la invariante de nombre no vacio.
     */
    public void renombrar(String nuevoNombre) {
        throw new UnsupportedOperationException("TODO: implementar");
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Usuario otro)) {
            return false;
        }
        return Objects.equals(id, otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
