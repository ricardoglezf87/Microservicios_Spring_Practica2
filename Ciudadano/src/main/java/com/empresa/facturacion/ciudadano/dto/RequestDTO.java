package com.empresa.facturacion.ciudadano.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestDTO {
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
