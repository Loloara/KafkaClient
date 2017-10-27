package com.loloara.ProducerClient;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit; 

public class KafkaProducerExample {
	private final static String TOPIC = "cluser";
	//private final static String BOOTSTRAP_SERVERS = "kafka-01:9092, kafka-02:9092, kafka-03:9092";
	private final static String BOOTSTRAP_SERVERS = "172.17.0.2:9092, 172.17.0.3:9092, 172.17.0.4:9092";
	private final static int messageNum = 25;		//전송 메세지 개수
	
	//Long, String type Producer 생성
	private static Producer<Long, String> createProducer(){
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaExampleProducer");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		return new KafkaProducer<Long, String>(props);
	}
	
	static void runProducer(final int sendMessageCount) throws Exception{
		final Producer<Long,String> producer = createProducer();
		long time = System.currentTimeMillis();
		final CountDownLatch countDownLatch = new CountDownLatch(sendMessageCount);
		
		try {
			for(long index = 0; index < sendMessageCount;index++) {
				final ProducerRecord<Long, String> record = new ProducerRecord<Long, String>(TOPIC, index, "I want that car number "+index);
				producer.send(record, (metadata, exception) -> {
					long elapsedTime = System.currentTimeMillis() - time;
					if(metadata != null) {
						System.out.println("sent record(key=" + record.key() + " value=" + record.value() + ") "
								+ "meta(partition=" + metadata.partition() + " offset=" + metadata.offset() + " time=" + elapsedTime);
					}else {
						exception.printStackTrace();
					}
					countDownLatch.countDown();
				});
			}
			countDownLatch.await(25, TimeUnit.SECONDS);
		} finally {
			producer.flush();
			producer.close();
		}
	}
	
	public static void main(String args[]) throws Exception {
		System.out.println("I'm PRODUCER");
		if(args.length == 0) {
			runProducer(messageNum);
		}else {
			runProducer(Integer.parseInt(args[0]));
		}
	}
}