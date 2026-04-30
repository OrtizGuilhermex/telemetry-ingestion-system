const { Kafka } = require('kafkajs');
const winston = require('winston');

// ── Logger (Configuração de logs no Console e Arquivo) ───────────
const logger = winston.createLogger({
    level: 'info',
    format: winston.format.combine(
        winston.format.timestamp(),
        winston.format.printf(({ timestamp, level, message }) => {
            return `[${timestamp}] ${level.toUpperCase()}: ${message}`;
        })
    ),
    transports: [
        new winston.transports.Console(),
        new winston.transports.File({ filename: 'alertas.log' })
    ]
});

// ── Kafka Configuration ──────────────────────────────────────────
const kafka = new Kafka({
    clientId: 'alert-service',
    brokers: ['127.0.0.1:9094'], // Usando IP para evitar problemas de DNS no Windows
    retry: { retries: 5 }
});

const consumer = kafka.consumer({ groupId: 'alert-service-group' });

// ── Lógica de Alerta por Severidade ──────────────────────────────
function processarAlerta(evento) {
    const emojis = { 
        CRITICA: '🚨', 
        ALTA: '⚠️', 
        MEDIA: '📊' 
    };

    const emoji = emojis[evento.severidade] || '❓';
    
    const msg = `${emoji} ALERTA ${evento.severidade} | ` +
                `Sensor: ${evento.sensorId} | ` +
                `Estação: ${evento.estacao} | ` +
                `Valor: ${evento.valorDetectado} | ` +
                `Tipo: ${evento.tipoAnomalia} | ` +
                `Threshold: ${evento.threshold}`;

    // Filtragem por nível de log do Winston
    if (evento.severidade === 'CRITICA') {
        logger.error(msg);
        // Ex: aqui dispararia um comando de parada de máquina (CLP)
    } else if (evento.severidade === 'ALTA') {
        logger.warn(msg);
    } else {
        logger.info(msg);
    }
}

// ── Main Execution ───────────────────────────────────────────────
async function iniciar() {
    try {
        await consumer.connect();
        logger.info('Alert Service conectado ao Kafka com sucesso!');

        await consumer.subscribe({
            topic: 'producao.anomalia.detectada',
            fromBeginning: false // Lê apenas as novas anomalias a partir de agora
        });

        await consumer.run({
            eachMessage: async ({ topic, partition, message }) => {
                try {
                    const payload = message.value.toString();
                    const evento = JSON.parse(payload);
                    processarAlerta(evento);
                } catch (err) {
                    logger.error(`Erro ao fazer parse da mensagem: ${err.message}`);
                }
            }
        });

    } catch (err) {
        logger.error(`Erro de conexão no Kafka: ${err.message}`);
    }
}

// Inicialização com tratamento de erro fatal
iniciar().catch(err => {
    logger.error(`Falha fatal no serviço de alertas: ${err.message}`);
    process.exit(1);
});