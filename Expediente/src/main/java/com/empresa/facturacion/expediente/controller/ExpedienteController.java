package com.empresa.facturacion.expediente.controller;

import com.empresa.facturacion.expediente.entities.Expediente;
import com.empresa.facturacion.expediente.repository.ExpedienteRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.empresa.facturacion.expediente.dto.RequestDTO;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import jakarta.validation.Valid;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.netty.http.client.HttpClient;

@RestController
@RequestMapping("/expediente")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE })
public class ExpedienteController {

    @Autowired
    ExpedienteRepository expedienteRepository;

    @Autowired
    WebClient.Builder webClientBuilder;

    HttpClient client = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .option(EpollChannelOption.TCP_KEEPIDLE, 300)
            .option(EpollChannelOption.TCP_KEEPINTVL, 60)
            .responseTimeout(Duration.ofSeconds(1))
            .doOnConnected(connection -> {
                connection.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS));
                connection.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS));
            });

    @GetMapping()
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(expedienteRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable long id) {

        Optional<Expediente> expediente = expedienteRepository.findById(id);

        if (expediente.isPresent()) {
            return ResponseEntity.ok(expediente.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable long id, @Valid @RequestBody Expediente input, BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }

        Optional<Expediente> expediente = expedienteRepository.findById(id);
        if (expediente.isPresent()) {
            Expediente newExpediente = expediente.get();
            newExpediente.setCiudadanoId(input.getCiudadanoId());
            newExpediente.setNotas(input.getNotas());
            newExpediente.setTipoPrestacion(input.getTipoPrestacion());
            newExpediente = expedienteRepository.save(newExpediente);
            return ResponseEntity.ok(newExpediente);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody RequestDTO input, BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }

        long ciudadanoId = getciudadanoId(input.getDniNie());

        if (ciudadanoId == 0) {
            ciudadanoId = createCiudadano(input);
        } else if (expedienteRepository.existsByCiudadanoIdAndTipoPrestacion(ciudadanoId, input.getTipoPrestacion())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Ya existe un expediente con este tipo de prestacion para este ciudadano.");
        }

        Expediente expediente = new Expediente();
        expediente.setCiudadanoId(ciudadanoId);
        expediente.setNotas(input.getNotas());
        expediente.setTipoPrestacion(input.getTipoPrestacion());
        expediente = expedienteRepository.save(expediente);

        return ResponseEntity.status(HttpStatus.CREATED).body(expediente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
        Optional<Expediente> expediente = expedienteRepository.findById(id);
        if (expediente.isPresent()) {
            expedienteRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/buscarPorTipoPrestacion")
    public ResponseEntity<?> findByTipoPrestacion(@RequestParam int tipoPrestacion) {
        List<Expediente> expediente = expedienteRepository.findByTipoPrestacion(tipoPrestacion);
        if (!expediente.isEmpty()) {
            return ResponseEntity.ok(expediente);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private Long getciudadanoId(String dni) {
        WebClient build = webClientBuilder.clientConnector(new ReactorClientHttpConnector(client))
                .baseUrl("http://localhost:8081/ciudadano")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        try {
            Long id = build.method(HttpMethod.GET)
                    .uri(uriBuilder -> uriBuilder
                            .path("/consultarIdByDNI")
                            .queryParam("dni", dni)
                            .build())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .map(jsonNode -> jsonNode.isNumber() ? jsonNode.asLong() : null)
                    .block();

            return id != null ? id : 0L;
        } catch (WebClientResponseException.NotFound e) {
            return 0L;
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    private Long createCiudadano(RequestDTO requestoDTO) {
        WebClient build = webClientBuilder.clientConnector(new ReactorClientHttpConnector(client))
                .baseUrl("http://localhost:8081/ciudadano")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        try {
            Long id = build.method(HttpMethod.POST)
                    .uri("/altaCiudadano")
                    .body(BodyInserters.fromValue(requestoDTO))
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .map(jsonNode -> jsonNode.isNumber() ? jsonNode.asLong() : null)
                    .block();
            return id != null ? id : 0L;
        } catch (WebClientResponseException.NotFound e) {
            System.out.println(e.getMessage());
            return 0L;
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
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
