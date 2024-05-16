package com.empresa.facturacion.expediente.dto;

import org.hibernate.validator.constraints.Range;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestDTO {

    @NotNull
    @Range(min = 1, max = 3, message = "El tipo de prestación solo tomar los valores (1,2,3)")
    private int tipoPrestacion;

    @Size(max = 80)
    private String notas;

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