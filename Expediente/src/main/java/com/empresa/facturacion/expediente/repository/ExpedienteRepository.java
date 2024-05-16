
package com.empresa.facturacion.expediente.repository;

import com.empresa.facturacion.expediente.entities.Expediente;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpedienteRepository extends JpaRepository<Expediente, Long> {
    List<Expediente> findByTipoPrestacion(int tipoPrestacion);

    Boolean existsByCiudadanoIdAndTipoPrestacion(long ciudadanoId, int TipoPrestacion);
}
