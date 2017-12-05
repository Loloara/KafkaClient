package com.loloara.ConsumerClient;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import java.util.Properties;
import java.util.Collections;

import java.io.Serializable;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.*;

import java.io.IOException;
import scala.Tuple2;

import java.sql.*;

import java.io.*;
import java.net.*;
import java.util.Date;
import java.text.*;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.FileSystem;
import java.io.BufferedWriter;

import org.apache.hadoop.conf.Configuration;

import kr.bydelta.koala.data.Sentence;
import kr.bydelta.koala.data.Word;
import kr.bydelta.koala.twt.Tagger;
import java.sql.PreparedStatement;

public class ConsumerClient implements Serializable {
	private String TOPIC;
	private final String BOOTSTRAP_SERVERS = "kafka-01:9092,kafka-02:9092,kafka-03:9092";
	private String keyword; //입력받은 keyword 저장 변수
	private int keyword_seq; // 입력받은 keyword seq 저장 변수
	private StringBuilder analysis_result = new StringBuilder(); // 형태소 분석 결과 저장
	
	ConsumerClient(final String TOPIC) {
		this.TOPIC = TOPIC;
	}
	
	// Long, String type Consumer 생성
	private Consumer<String, String> createConsumer() {
		final Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, TOPIC);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		// Producer Client와는 다르게 key와 value를 Deserializer로 세팅한다.

		// create the consumer using props
		final Consumer<String, String> consumer = new KafkaConsumer<String, String>(props);

		// Topic을 구독
		consumer.subscribe(Collections.singletonList(TOPIC));
		return consumer;
	}

	public void execute_spark_store_db(String inputPath, String input_keyword, int input_keyword_seq) {

		SparkConf spark_conf = new SparkConf().setMaster("local").setAppName("WordCount"); //spark 연결 설정
		JavaSparkContext sc = new JavaSparkContext(spark_conf); //spark 연결

		JavaRDD<String> input = sc.textFile(inputPath); //분석 파일 불러오기
		JavaRDD<String> words = input.flatMap(new FlatMapFunction<String, String>() {

			public Iterator<String> call(String x) {
				return Arrays.asList(x.split(" ")).iterator(); //공백단위로 split
			}
		});
		JavaPairRDD<String, Integer> counts = words.mapToPair(new PairFunction<String, String, Integer>() {
			public Tuple2<String, Integer> call(String x) {
				return new Tuple2(x, 1); //분석 결과 tuple로 생성
			}
		}).reduceByKey(new Function2<Integer, Integer, Integer>() {
			public Integer call(Integer x, Integer y) throws Exception {
				return x + y; //word count 증가
			}
		});
		
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String dbInfo = "jdbc:mysql://192.168.10.210:3306/KCC_LAB";
			String dbID = "root";
			String dbPW = "1q2w3e4r%T";

			java.sql.Connection conn = java.sql.DriverManager.getConnection(dbInfo, dbID, dbPW); //DB 연결
			String dbCommand = "insert into Word (keyword, seq, word, frequency) values(?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(dbCommand);
			for (Tuple2<String, Integer> data : counts.collect()) {
				String word_data = data._1(); //분석 결과에서 첫번째 값인 Word 저장
				int count_value = Integer.parseInt(data._2().toString()); //분석 결과에서 두번째 값인 Count 저장

				ps.setString(1, input_keyword); //query에 keyword 저장
				ps.setInt(2, input_keyword_seq); //query에 seq 저장
				ps.setString(3, word_data); //query에 word data 저장
				ps.setInt(4, count_value); //query에 count value 저장
				ps.addBatch();
			}
			ps.executeBatch(); //query 실행
			ps.close();
			conn.close();
			
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		sc.stop();
		sc.close();
		
	}// spark word count 함수 : jaehwan

	public String morpheme_analysis(String value) {
		StringBuilder result = new StringBuilder(); //형태소 분석 결과 저장 변수

		List<String> dataList = new ArrayList<String>(); //전체 데이터 저장 변수

		Tagger tagger = new Tagger();

		List<Sentence> sentences = tagger.jTag(value); //단어별 분류 저장 변수

		for (int i = 0; i < sentences.size(); i++) {
			Sentence tmp = sentences.get(i);
			List<Word> words = tmp.jNouns(); //명사 단위로 단어 저장
			
			for (int j = 0; j < words.size(); j++) {
				Word word = words.get(j);
				String strWord1 = word.toString();
				String strWord2 = strWord1.split(" ")[1]; //공백 단위로 분류
				String[] strWord3 = strWord2.split("\\)");

				for (int k = 0; k < strWord3.length; k++) {
					try {
						String[] chkS = strWord3[k].split("/");
						if (chkS[1].equals("NNG(Noun") && (chkS[0].length() > 1)) { //분석결과가 명사이고 한 문자 이상이면
							dataList.add(chkS[0]); //데이터 리스트에 추가
						}
					} catch (Exception e) {
						continue;
					}
				}
			}
		}

		for (int i = 0; i < dataList.size(); i++) {
			if (!result.toString().contains(dataList.get(i))) {
				result.append(dataList.get(i)).append(" "); //한 분석 값에서 중복된 데이터가 없으면 저장
			}
		}
		return result.toString(); //형태소 분석 결과 반환
	}//형태소 분석 함수 : jaehwan, loloara

	public void storing_hdfs(String hdfs_input_path, Path hdfs_input_filePath, String hdfs_data) {
		OutputStream out = null;
		FileSystem fs;
		try {
			Configuration hdfs_conf = new Configuration();
			hdfs_conf.set("fs.defaultFS", "hdfs://Master:8020"); //hdfs연결 설정

			fs = FileSystem.get(URI.create(hdfs_input_path), hdfs_conf); //Filesystem을 통해 hdfs 연결

			if (!fs.exists(hdfs_input_filePath)) {
				out = fs.create(hdfs_input_filePath); //파일이 기존에 존재하지 않으면 새롭게 생성
			} else {
				out = fs.append(hdfs_input_filePath); //파일이 기존에 존재하면 해당 파일에 추가
			}

			BufferedWriter br = new BufferedWriter(new OutputStreamWriter(out, "UTF-8")); //설정 경로에 BufferedWriter 연결
			br.write(hdfs_data); //데이터 삽입
			br.newLine();
			br.close();
			fs.close();
		} catch (IOException e) {
			e.printStackTrace();
		} //형태소 분석 값을 hdfs에 저장하는 함수 : jaehwan
	}

	public void runConsumer() throws InterruptedException {
		final Consumer<String, String> consumer = createConsumer();
		final int giveUp = 100; // 값이 안들어왔을 때 몇초 동안 기다릴지 설정
		int noRecordsCount = 0;
		boolean kafka_pass = false; //분석을 실행할 지를 결정하는 변수(consumerRecords.count만 사용할 경우 message 수신 대기 상태일 때도 분석을 실행하는 issue 발생)

		while (true) {
			final ConsumerRecords<String, String> consumerRecords = consumer.poll(1000); //1초 간격으로 message 수신 대기
			if (consumerRecords.count() == 0 && noRecordsCount > giveUp) {
				// noRecordsCount++; never give up
					break;
			}
			
			if (consumerRecords.count() != 0) { //만약 Kafka message가 존재한다면
				consumerRecords.forEach(record -> { // 받은 record를 비동기로 출력해준다.
	
					String message_key = record.key().toString();
					String[] message_key_array = message_key.split("-"); //record.key 값에 포함된 정보를 분류
					keyword = message_key_array[0]; //keyword 값 저장
					keyword_seq = Integer.parseInt(message_key_array[1]); //seq 값 저장
	
					analysis_result.append(morpheme_analysis(record.value().toString())); //전송받은 메시지를 형태소 분석		
				});
				kafka_pass = true; //분석 실행 여부를 변경
				consumer.commitAsync();
			}else if(kafka_pass){ //Kafka message를 전부 수신하였고 분석실행 여부가 true라면
				Date dt = new Date();
				SimpleDateFormat formatter = new SimpleDateFormat ( "yyyy-MM-dd-HH-mm-ss");
				String collect_time = formatter.format(dt); //경로 설정에서 활용하기 위해 현재 시간을 저장
				
				StringBuilder input_directory_path = new StringBuilder(); // 형태소 분석 결과 및 spark 분석 input 디렉터리 위치 지정 변수
				input_directory_path.append("hdfs://Master:8020/input_kafka").append("_").append(keyword).append("_").append(keyword_seq); //keyword와 seq를 활용하여 형태소 분석 값을 저장할 hdfs 경로 지정

				StringBuilder txt_filePath_make = new StringBuilder(input_directory_path);
				txt_filePath_make.append("/").append(collect_time).append(".txt"); //input_path와 현재시간을 통해 형태소 분석 값을 저장할 txt파일 경로 지정
				
				Path input_txt_filePath = new Path(txt_filePath_make.toString());
				
				storing_hdfs(input_directory_path.toString(), input_txt_filePath, analysis_result.toString()); //input_path 디렉토리에 input_filePath의 경로 및 txt 파일 이름으로 저장
				execute_spark_store_db(input_txt_filePath.toString(), keyword, keyword_seq); //형태소 분석된 값을 기반으로 spark 분석 실행
				analysis_result.setLength(0);//형태소 분석 값 초기화
				kafka_pass = false; //분석 완료시 분석 실행 여부를 false로 변경
			}
		}
		
		consumer.close();
		System.out.println("DONE");
	}

}