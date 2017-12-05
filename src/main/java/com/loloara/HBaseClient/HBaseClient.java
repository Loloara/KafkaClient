package com.loloara.HBaseClient;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import com.google.protobuf.ServiceException;

import java.util.Properties;
import java.util.Collections;
import java.io.IOException;
import java.io.Serializable;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.client.Table;


public class HBaseClient implements Serializable {
	private String TOPIC;
	private final String BOOTSTRAP_SERVERS = "kafka-01:9092,kafka-02:9092,kafka-03:9092";
	private String keyword;
	private String keyvalue;
	private int keyword_seq;
	
	private Configuration hbase_conf;
	private Connection connection;
	private HBaseAdmin admin;
	private Table hTable;

	HBaseClient(final String TOPIC) {
		this.TOPIC = TOPIC;
	}
	

	// Long, String type Consumer 생성
	private Consumer<String, String> createConsumer() {
		final Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, TOPIC+"-hbase");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		// Producer Client와는 다르게 key와 value를 Deserializer로 세팅한다.

		// create the consumer using props
		final Consumer<String, String> consumer = new KafkaConsumer<String, String>(props);
		
		hbase_conf = HBaseConfiguration.create(); // hbase configuration 객체 생성
		hbase_conf.clear();
		hbase_conf.set("hbase.zookeeper.quorum", "192.168.10.210");
		hbase_conf.set("hbase.zookeeper.property.clientPort", "2181");
		hbase_conf.set("hbase.master", "192.168.10.210:60000"); //hbase 연결 정보 설정

		try {
			connection = ConnectionFactory.createConnection(hbase_conf);
			admin = (HBaseAdmin) connection.getAdmin(); //hbase 관리자 연결

			HBaseAdmin.checkHBaseAvailable(hbase_conf); //hbase 실행 여부 검사

			hTable = connection.getTable(TableName.valueOf("Kafka_message")); //hbase 내의 테이블에 연결
		} catch (IOException | ServiceException e) {
			e.printStackTrace();
		} //hbase 연결

		
		// Topic을 구독
		consumer.subscribe(Collections.singletonList(TOPIC));
		return consumer;
	}
	

	public void storing_origin_msg_hbase(String hbase_keyvalue, String hbase_keyword, int hbase_key_seq, String hbase_data_value) {
		try {
			Put p = new Put(Bytes.toBytes(hbase_keyvalue)); // row key를 key value로 지정하여 put 객체 생성
			
			StringBuilder Keyword = new StringBuilder(); //Column name을 지정하기 위한 변수
			Keyword.append(hbase_keyword).append("_").append(hbase_key_seq); //keyword 값과 key_seq를 활용하여 Column name 생성

			p.addColumn(Bytes.toBytes("Kafka_data"), Keyword.toString().getBytes("utf-8"), hbase_data_value.getBytes("utf-8"));
			// p 객체에 Column Family, Column name, value 순으로 입력

			hTable.put(p); // 입력된 내용들을 테이블에 저장

		} catch (MasterNotRunningException e) { // HBase 실행 불가 원인 출력
			System.out.println("HBase is not running!");
			System.out.println("exception: " + e.getMessage());
			System.exit(1);
		} catch (Exception ce) {
			ce.printStackTrace();
		}
	}//원문 hbase 저장 함수 : jaehwan

	public void runConsumer() throws InterruptedException {
		final Consumer<String, String> consumer = createConsumer();
		final int giveUp = 100; // 값이 안들어왔을 때 몇초 동안 기다릴지 설정
		int noRecordsCount = 0;
		
		
		while (true) {
			final ConsumerRecords<String, String> consumerRecords = consumer.poll(Long.MAX_VALUE);
			if (consumerRecords.count() == 0) {
				// noRecordsCount++; never give up
				if (noRecordsCount > giveUp)
					break;
				else
					continue;
			}

			consumerRecords.forEach(record -> {

				String message_key = record.key().toString();
				String[] message_key_array = message_key.split("-");
				keyword = message_key_array[0];
				keyword_seq = Integer.parseInt(message_key_array[1]);
				keyvalue = message_key_array[2];
				
				storing_origin_msg_hbase(keyvalue, keyword, keyword_seq, record.value()); //전송받은 원문을 hbase에 저장
			});
			// append finish
			System.out.println(keyword + "storing success!");
			consumer.commitAsync();
		}
		consumer.close();
		try {
			hTable.close();
			admin.close();
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("DONE");
	}

}
