package br.com.weg.sensor_ingestion_service.controller;

import br.com.weg.sensor_ingestion_service.model.SensorLeituraEvent;
import br.com.weg.sensor_ingestion_service.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;

@RestController
@RequestMapping("/api/sensores")
@RequiredArgsConstructor
public class SensorController {
    private final KafkaProducerService producerService;

    @PostMapping("/leitura")
    public ResponseEntity<String> receberLeitura(@RequestBody SensorLeituraEvent evento) {
        // Garante que o timestamp está preenchido
        if (evento.getTimestamp() == null) {
            evento.setTimestamp(Instant.now());
        }
        producerService.publicarLeitura(evento);
        return ResponseEntity.accepted().body("Leitura recebida e publicada no Kafka");
    }

    // Endpoint de health check
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("sensor-ingestion-service: UP");
    }
}