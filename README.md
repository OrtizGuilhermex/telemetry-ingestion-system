# 🏭 Industrial IoT Monitoring System (WEG)

Sistema de monitoramento de sensores industriais em tempo real, utilizando arquitetura de microsserviços orientada a eventos (EDA), processamento de stream e armazenamento em série temporal.

---

## 🏗️ Arquitetura do Sistema

O sistema é composto por uma pipeline de dados de alta performance:

- **Simulador de Sensores (Java/Python):** Gera telemetria (Temperatura, Vibração, etc).
- **Apache Kafka:** Backbone de mensagens dividindo dados brutos e anomalias.
- **Data Storage Service (Java/Spring Boot):** Consome dados do Kafka e persiste no InfluxDB.
- **Anomaly Detection Service (Python/Faust):** Processamento de stream para detectar desvios em tempo real.
- **Dashboard API (Go/Gin):** Serve dados quentes via cache em Redis.
- **Frontend (React/Next.js):** Visualização em tempo real para o operador.
- **Monitoramento (Grafana/Prometheus):** Dashboards de infraestrutura e alertas.

---

## 🛠️ Tecnologias Utilizadas

| Camada        | Tecnologia                          |
|---------------|-------------------------------------|
| Linguagens    | Java 17, Python 3.13, Go 1.22       |
| Mensageria    | Apache Kafka (Redpanda/Kafka UI)    |
| Bancos de Dados | InfluxDB (Time Series), Redis (Cache) |
| Frameworks    | Spring Boot 3.x, Faust, Gin Gonic  |
| Infraestrutura | Docker & Docker Compose            |

---

## 🚀 Como Rodar o Projeto

### 1. Pré-requisitos

- Docker e Docker Compose instalados.
- Java 17, Python 3.x e Go instalados localmente (para desenvolvimento).

### 2. Subindo a Infraestrutura

```bash
docker-compose up -d
```

Isso iniciará o Kafka, Zookeeper, InfluxDB, Redis e Kafka UI.

### 3. Iniciando os Microsserviços

**Data Storage (Java):**
```bash
cd data-storage-service
./mvnw spring-boot:run
```

**Anomaly Detection (Python):**
```bash
cd anomaly-detection-service
faust -A app.main worker -l info
```

**Dashboard API (Go):**
```bash
cd dashboard-api-service
go run main.go
```

---

## 📡 Endpoints Principais

| Serviço | Endpoint |
|---------|----------|
| API de Anomalias | `GET http://localhost:8083/api/anomalias/recentes` |
| Health Check | `GET http://localhost:8082/actuator/health` |
| Kafka UI | `http://localhost:8080` |
| InfluxDB UI | `http://localhost:8086` |

---

## 🧪 Validação de Dados (Payload Exemplo)

Para testar a integração, envie o seguinte JSON via Kafka UI no tópico `producao.sensor.leitura`:

```json
{
  "sensorId": "SENSOR-LUIZ-01",
  "valor": 85.0,
  "timestamp": "2026-04-30T16:00:00Z",
  "estacao": "ESTACAO-01",
  "tipoMedicao": "TEMPERATURA",
  "unidade": "CELSIUS"
}
```

---

## 📊 Monitoramento e Dashboards

- **Grafana:** Conectado ao InfluxDB para gerar gráficos históricos de performance dos ativos.
- **Alertas:** Configurados para disparar quando a severidade da anomalia for `CRITICAL`.

---

> Desenvolvido para fins educacionais — **SENAI AI MIDS 2024/2**