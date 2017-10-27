package com.loloara.ConsumerClient;

public class app {

	public static void main(String[] args) throws Exception{
		System.out.println("I'm Consumer");
		
		ConsumerClient consumer = new ConsumerClient("cluser");
		consumer.runConsumer();
	}

}
