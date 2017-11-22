package com.loloara.ConsumerClient;

public class app {

	public static void main(String[] args) throws Exception{
		System.out.println("I'm Consumer");
		final String TOPIC = "Tweets";
		ConsumerClient consumer = new ConsumerClient(TOPIC);
		consumer.runConsumer();
	}

}
