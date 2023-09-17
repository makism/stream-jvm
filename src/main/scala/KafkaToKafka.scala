import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.connector.kafka.sink.{KafkaRecordSerializationSchema, KafkaSink}
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.functions.source.datagen.{DataGenerator, DataGeneratorSource}

object KafkaToKafka extends App {
  val env = StreamExecutionEnvironment.getExecutionEnvironment

  // Adding KafkaSource
  val kafkaSource = KafkaSource
    .builder()
    .setBootstrapServers("bender:9092")
    .setTopics("flink-example")
    .setGroupId("flink-consumer-group")
    .setStartingOffsets(OffsetsInitializer.latest())
    .setValueOnlyDeserializer(new SimpleStringSchema())
    .build()

  val serializer = KafkaRecordSerializationSchema
    .builder()
    .setValueSerializationSchema(new SimpleStringSchema())
    .setTopic("flink-example-out")
    .build()

  // Adding KafkaSink
  val kafkaSink = KafkaSink.builder()
    .setBootstrapServers("bender:9092")
    .setRecordSerializer(serializer)
    .build()

  val lines = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "Kafka Source")
  lines.print()

  lines.sinkTo(kafkaSink)

  env.execute("Read from Kafka")
}