package com.loloara.ProducerClient;

public class app {
	public static void main(String[] args) throws Exception{
		System.out.println("I'm PRODUCER");
		ProducerClient producer = new ProducerClient();
		if(args.length == 0)
			producer.runProducer("cluser", 25);
		else{
			producer.runProducer("cluser", Integer.parseInt(args[0]));
		}
	}
}
