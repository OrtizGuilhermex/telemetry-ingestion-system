package com.ctw.data_storage_service.model;

import lombok.Data;
import java.time.Instant;

@Data
public class SensorLeituraEvent {
    private String sensorId;
    private String estacao;
    private String tipoMedicao;
    private Double valor;
    private String unidade;
    private Instant timestamp;
}
