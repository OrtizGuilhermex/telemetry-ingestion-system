package com.ctw.data_storage_service.consumer;

import com.ctw.data_storage_service.model.SensorLeituraEvent;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

@Component
public class SensorStorageConsumer {

    private static final Logger log = LoggerFactory.getLogger(SensorStorageConsumer.class);

    @Value("${influx.url}") 
    private String url;

    @Value("${influx.token}") 
    private String token;

    @Value("${influx.org}") 
    private String influxOrg; // MUDEI O NOME AQUI PARA influxOrg

    @Value("${influx.bucket}") 
    private String bucket;

    private WriteApiBlocking writeApi;

    @PostConstruct
    public void init() {
        try {
            // Usando influxOrg aqui também
            InfluxDBClient client = InfluxDBClientFactory.create(url, token.toCharArray(), influxOrg, bucket);
            this.writeApi = client.getWriteApiBlocking();
            log.info("Conectado ao InfluxDB em {}", url);
        } catch (Exception e) {
            log.error("Erro ao conectar no InfluxDB: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "producao.sensor.leitura", groupId = "data-storage-group")
    public void consumir(SensorLeituraEvent evento) {
        try {
            Point point = Point
                    .measurement(evento.getTipoMedicao())
                    .addTag("sensorId", evento.getSensorId())
                    .addTag("estacao", evento.getEstacao())
                    .addField("valor", evento.getValor())
                    .time(evento.getTimestamp(), WritePrecision.NS);

            writeApi.writePoint(point);
            
            log.info("Persistido no InfluxDB: {} = {}", evento.getSensorId(), evento.getValor());
        } catch (Exception e) {
            log.error("Erro ao persistir leitura no InfluxDB: {}", e.getMessage());
        }
    }
}