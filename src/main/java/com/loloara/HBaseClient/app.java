package com.loloara.HBaseClient;

public class app {

	public static void main(String[] args) throws Exception{
		System.out.println("I'm HBaseClient");
		final String TOPIC = "Tweets";
		HBaseClient consumer = new HBaseClient(TOPIC);
		consumer.runConsumer();
	}

}