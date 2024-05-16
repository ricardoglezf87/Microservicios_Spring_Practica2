
package com.empresa.facturacion.ciudadano.repository;

import com.empresa.facturacion.ciudadano.entities.Ciudadano;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CiudadanoRepository extends JpaRepository<Ciudadano, Long> {
    Ciudadano findByDniNie(String dni);
}
