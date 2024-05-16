package com.empresa.facturacion.expediente.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Date;
import lombok.Data;

@Data
@Entity
public class Expediente {
    
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;
    
    @NotNull
    private int tipoPrestacion;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date createAt;
    
    @NotNull
    private long ciudadanoId;
    
    @Size(max=80)
    private String notas;
    
    @PrePersist
    protected void onCreate()
    {
        this.createAt = new Date();
    }
}
