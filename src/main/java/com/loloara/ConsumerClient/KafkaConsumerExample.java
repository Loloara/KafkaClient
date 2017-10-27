package com.loloara.ConsumerClient;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import java.util.Properties;
import java.util.Collections;

public class KafkaConsumerExample {
	private final static String TOPIC = "cluser";
	private final static String BOOTSTRAP_SERVERS = "kafka-01:9092, kafka-02:9092, kafka-03:9092";	
	
	//Long, String type Consumer 생성
	private static Consumer<Long, String> createConsumer(){
		final Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaExampleConsumer");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		
		//create the consumer using props.
		final Consumer<Long, String> consumer = new KafkaConsumer<Long, String>(props);
		
		//subscribe to the topic
		consumer.subscribe(Collections.singletonList(TOPIC));		
		return consumer;
	}
	
	static void runConsumer() throws InterruptedException{
		final Consumer<Long, String> consumer = createConsumer();
		final int giveUp = 100;
		int noRecordsCount = 0;
		
		while(true) {
			final ConsumerRecords<Long, String> consumerRecords = consumer.poll(1000);
			if(consumerRecords.count()==0) {
				noRecordsCount++;
				if(noRecordsCount > giveUp) break;
				else continue;
			}
			
			consumerRecords.forEach(record -> {
				System.out.println("Consumer Record: (" + record.key() + ", " + record.value() + ", " + record.partition() + ", " + record.offset() + ")");
			});
			consumer.commitAsync();
		}
		consumer.close();
		System.out.println("DONE");
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println("I'm Consumer");
		runConsumer();
	}

}
