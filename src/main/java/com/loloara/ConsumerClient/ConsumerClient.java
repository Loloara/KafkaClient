package com.loloara.ConsumerClient;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import java.util.Properties;
import java.util.Collections;

public class ConsumerClient {
	private static String TOPIC;
	private final static String BOOTSTRAP_SERVERS = "kafka-01:9092,kafka-02:9092,kafka-03:9092";
	
	ConsumerClient(final String TOPIC){
		ConsumerClient.TOPIC = TOPIC;
	}

	//Long, String type Consumer 생성
	private static Consumer<Long, String> createConsumer(){
		final Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, TOPIC);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		//Producer Client와는 다르게 key와 value를 Deserializer로 세팅한다.
			
		//create the consumer using props
		final Consumer<Long, String> consumer = new KafkaConsumer<Long, String>(props);
		
		//Topic을 구독
		consumer.subscribe(Collections.singletonList(TOPIC));		
		return consumer;
	}
	
	public void runConsumer() throws InterruptedException{
		final Consumer<Long, String> consumer = createConsumer();
		final int giveUp = 100;		//값이 안들어왔을 때 몇초 동안 기다릴지 설정
		int noRecordsCount = 0;
		
		while(true) {
			final ConsumerRecords<Long, String> consumerRecords = consumer.poll(1000);
			if(consumerRecords.count()==0) {
				//noRecordsCount++; never give up
				if(noRecordsCount > giveUp) break;
				else continue;
			}
			
			consumerRecords.forEach(record -> {		//받은 record를 비동기로 출력해준다.
				System.out.println("get record:(key=" + record.key() + " value=" + record.value() + ") "
						+ "meta:(partition=" + record.partition() + " offset=" + record.offset() + ")");
			});
			consumer.commitAsync();
		}
		consumer.close();
		System.out.println("DONE");
	}
}