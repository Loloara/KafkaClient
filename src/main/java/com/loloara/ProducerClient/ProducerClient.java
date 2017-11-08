package com.loloara.ProducerClient;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.serialization.DoubleSerializer;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;		//비동기 처리를 위한 라이브러리
import java.util.concurrent.TimeUnit; 
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ProducerClient implements Job{
	private final static String TOPIC = "weather";
	//private final static String BOOTSTRAP_SERVERS = "kafka-01:9092,kafka-02:9092,kafka-03:9092";
	private final static String BOOTSTRAP_SERVERS = "kafka:8001,kafka:8002,kafka:8003";
	//private final static String BOOTSTRAP_SERVERS = "222.233.239.128:8001, 222.233.239.128:8002";
	//private final static String BOOTSTRAP_SERVERS = "54.175.21.137:8001, 54.175.21.137:8002";
	
	public void execute(JobExecutionContext context) {
		ForecastTown forecast = new ForecastTown();
		try {
			runProducer(TOPIC, forecast.getTownForecastFromJSON());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//Long, String type Producer 생성
	private static Producer<String, Double> createProducer(){
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaExampleProducer");	//임의의 Client ID
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());		//key 값 타입 설정
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, DoubleSerializer.class.getName());	//value 값 타입 설정
				
		return new KafkaProducer<String, Double>(props);
	}
	
	public void runProducer(final String TOPIC, JSONArray items) throws Exception{
		String key ="";
		String value = "";
		JSONObject obj;
		
		final Producer<String, Double> producer = createProducer();
		long time = System.currentTimeMillis();
		final CountDownLatch countDownLatch = new CountDownLatch(items.size());
		
		try {
			for(int i = 0; i < items.size();i++) {
				obj = (JSONObject) items.get(i);
				key = "baseDate:" + obj.get("baseDate").toString() + ";baseTime:" + obj.get("baseTime").toString() + ";category:" + obj.get("category").toString();
				value = obj.get("obsrValue").toString();
				
				
				final ProducerRecord<String, Double> record = new ProducerRecord<String, Double>(TOPIC, key, Double.parseDouble(value));
				producer.send(record, (metadata, exception) -> {
					long elapsedTime = System.currentTimeMillis() - time;
					if(metadata != null) {
						System.out.println("sent record(key=" + record.key() + " value=" + record.value() + ") "
								+ "meta:(partition=" + metadata.partition() + " offset=" + metadata.offset() + " time=" + elapsedTime);
					}else {
						exception.printStackTrace();
					}
					countDownLatch.countDown();
				});
			}
			countDownLatch.await(items.size(), TimeUnit.SECONDS);
		}finally {
			producer.flush();
			producer.close();
		}
	}
}