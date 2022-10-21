package icu.harx

import org.apache.kafka.clients.producer.{ProducerRecord, RecordMetadata}
import zio.*
import zio.kafka.producer.*
import zio.kafka.serde.*
import zio.stream.*

object ProducerApp extends ZIOAppDefault {

  // 配置 kafka 生产者连接
  val producerSettings: ProducerSettings = ProducerSettings(List("localhost:9092"))

  // 创建生产者
  val producer: RLayer[Scope, Producer] = ZLayer.scoped(Producer.make(producerSettings))

  // 产生数据流
  val recordStream: UStream[ProducerRecord[String, String]] =
    ZStream
      .fromSchedule(Schedule.spaced(1.second))
      // 最终映射为 ProducerRecord
      .map { duration => new ProducerRecord(TOPIC, "duration", s"${duration} sec") }

  // zio-kafka 封装成的 produce pipeline
  val producePipeline: ZPipeline[Producer, Throwable, ProducerRecord[String, String], RecordMetadata] =
    Producer.produceAll(Serde.string, Serde.string)

  override def run: URIO[Scope, ExitCode] =
    // 将数据流送入 produce pipeline, 运行副作用即可实现向 kafka 传入消息
    (recordStream >>> producePipeline).runDrain
      .provideLayer(producer)
      .exitCode

}
