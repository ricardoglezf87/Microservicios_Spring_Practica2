package com.empresa.facturacion.ciudadano.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
public class Ciudadano {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotNull
    @Size(max = 20)
    private String nombre;

    @NotNull
    @Size(max = 30)
    private String apellido1;

    @NotNull
    @Size(max = 30)
    private String apellido2;

    @NotNull
    @Size(max = 9)
    private String dniNie;

    @NotNull
    @Size(max = 40)
    @Email
    private String email;
}
