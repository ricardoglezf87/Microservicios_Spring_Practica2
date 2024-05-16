package com.empresa.facturacion.ciudadano.controller;

import com.empresa.facturacion.ciudadano.dto.RequestDTO;
import com.empresa.facturacion.ciudadano.entities.Ciudadano;
import com.empresa.facturacion.ciudadano.repository.CiudadanoRepository;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ciudadano")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST })
public class CiudadanoController {

    @Autowired
    CiudadanoRepository ciudadanoRepository;

    @GetMapping()
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(ciudadanoRepository.findAll());
    }

    @PostMapping("/altaCiudadano")
    public ResponseEntity<?> create(@Valid @RequestBody RequestDTO input, BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }

        Ciudadano ciudadano = new Ciudadano();
        ciudadano.setDniNie(input.getDniNie());
        ciudadano.setNombre(input.getNombre());
        ciudadano.setApellido1(input.getApellido1());
        ciudadano.setApellido2(input.getApellido1());
        ciudadano.setEmail(input.getEmail());

        ciudadano = ciudadanoRepository.save(ciudadano);
        return ResponseEntity.status(HttpStatus.CREATED).body(ciudadano.getId());
    }

    @GetMapping("/consultarCiudadano")
    public ResponseEntity<?> get(@RequestParam long id) {
        Optional<Ciudadano> ciudadano = ciudadanoRepository.findById(id);
        if (ciudadano.isPresent()) {
            return ResponseEntity.ok(ciudadano.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/consultarIdByDNI")
    public ResponseEntity<?> getIdByDni(@RequestParam String dni) {
        Ciudadano ciudadano = ciudadanoRepository.findByDniNie(dni);
        if (ciudadano != null) {
            return ResponseEntity.ok(ciudadano.getId());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
