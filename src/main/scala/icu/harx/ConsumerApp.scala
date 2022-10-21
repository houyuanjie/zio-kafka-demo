package icu.harx

import zio.*
import zio.kafka.consumer.*
import zio.kafka.serde.*
import zio.stream.*

import java.io.IOException

object ConsumerApp extends ZIOAppDefault {

  // 配置 kafka 消费者连接
  val consumerSettings: ConsumerSettings =
    ConsumerSettings(List("localhost:9092"))
      .withGroupId("zio-kafka-demo-consumer-group")

  // 创建消费者
  val consumer: RLayer[Scope, Consumer] = ZLayer.scoped(Consumer.make(consumerSettings))

  // 订阅 kafka 主题
  val subscription: Subscription = Subscription.topics(TOPIC)

  // 从主题中取出数据流
  val recordStream: ZStream[Consumer, Throwable, CommittableRecord[String, String]] =
    Consumer
      .subscribeAnd(subscription)
      .plainStream(Serde.string, Serde.string)

  // 消费数据的逻辑
  val consumerSink: Sink[IOException, CommittableRecord[String, String], Nothing, Unit] =
    ZSink.foreach { cr =>
      Console.printLine(s"[ConsumerApp] ${cr.key} : ${cr.value}")
    }

  override def run: URIO[Scope, ExitCode] =
    // 将数据流送入消费逻辑
    (recordStream >>> consumerSink)
      .provideLayer(consumer)
      .exitCode

}
