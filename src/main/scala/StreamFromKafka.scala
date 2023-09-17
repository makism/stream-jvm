import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.{DeserializationSchema, SimpleStringSchema}
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.connector.kafka.sink.{KafkaRecordSerializationSchema, KafkaSink}
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
import org.apache.flink.streaming.api.scala._

case class ApiEvent(id: Long, weight: Float, method: String, path: String, service: String)

class ApiEventDeserializer extends DeserializationSchema[ApiEvent] {
  override def deserialize(message: Array[Byte]): ApiEvent = {
    val mapper = JsonMapper.builder()
      .addModule(DefaultScalaModule)
      .build()

    val api_event = mapper.readValue(message, classOf[ApiEvent])

    api_event
  }

  override def isEndOfStream(nextElement: ApiEvent): Boolean = false

  override def getProducedType: TypeInformation[ApiEvent] = {
    TypeExtractor.getForClass(classOf[ApiEvent])
  }
}

object StreamFromKafka extends App {
  val env = StreamExecutionEnvironment.getExecutionEnvironment

  val kafkaSource = KafkaSource
    .builder()
    .setBootstrapServers("bender:9092")
    .setTopics("api_events")
    .setGroupId("grp-api_events_fast")
    .setStartingOffsets(OffsetsInitializer.earliest())
    .setValueOnlyDeserializer(new ApiEventDeserializer)
    .build()

  val serializer = KafkaRecordSerializationSchema
    .builder()
    .setValueSerializationSchema(new SimpleStringSchema())
    .setTopic("flink-example-out")
    .build()

  val kafkaSink = KafkaSink.builder()
    .setBootstrapServers("bender:9092")
    .setRecordSerializer(serializer)
    .build()

  val lines = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "Kafka Source")
  lines.print()

//  lines.sinkTo(kafkaSink)

  env.execute("Read from Kafka")
}