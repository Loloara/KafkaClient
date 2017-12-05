package com.loloara.ProducerClient;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;		//비동기 처리를 위한 라이브러리
import java.util.concurrent.TimeUnit; 
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ProducerClient implements Job{
	private final String TOPIC = "Tweets";
	private final String BOOTSTRAP_SERVERS = "kafka-01:9092,kafka-02:9092,kafka-03:9092";
	private String[] query;	//0: keyword, 1: seq, 2: sinceId
	
	public void execute(JobExecutionContext context) {
		MySQLConn mysql = new MySQLConn();
		query = mysql.getKeyword();
		long sinceId = Long.parseLong(query[2]);
		
		TwitterSearch twitter = new TwitterSearch();
		JSONArray tweets = twitter.runSearchingKeyword(query[0], sinceId);
		if(tweets.size() != 0) {
			sinceId = (Long) ((JSONObject) tweets.get(0)).get("id");
		}
		
		try {
			runProducer(TOPIC, tweets);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			System.out.println("keyword: " + query[0] + "-" + query[1]);
			System.out.println("tweets length: " + tweets.size());
			mysql.addCountSinceIdToKeyword(tweets.size(), Integer.parseInt(query[1]), sinceId);
		}
	}
	
	//Long, String type Producer 생성
	private Producer<String, String> createProducer(){
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		props.put(ProducerConfig.CLIENT_ID_CONFIG, TOPIC);	//임의의 Client ID
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());		//key 값 타입 설정
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());	//value 값 타입 설정
		
		props.put(ProducerConfig.BATCH_SIZE_CONFIG, 10000);
		
		return new KafkaProducer<String, String>(props);
	}
	
	public void runProducer(final String TOPIC, JSONArray items) throws Exception{
		String key;
		String value;
		JSONObject obj;
		
		final Producer<String, String> producer = createProducer();
		long time = System.currentTimeMillis();
		final CountDownLatch countDownLatch = new CountDownLatch(items.size());
		
		try {
			for(int i = 0; i < items.size();i++) {
				obj = (JSONObject) items.get(i);
				key = query[0]+"-"+query[1]+"-"+Long.toString((long) obj.get("id"));
				value = (String) obj.get("text");
				
				
				final ProducerRecord<String, String> record = new ProducerRecord<String, String>(TOPIC, key, value);
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