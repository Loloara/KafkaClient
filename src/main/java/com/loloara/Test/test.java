package com.loloara.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import kr.bydelta.koala.data.Sentence;
import kr.bydelta.koala.data.Word;
import kr.bydelta.koala.twt.Tagger;

public class test {
	private static long sinceId = 0L;
	public static void twitterTest() {
		TwitterSearch twitter = new TwitterSearch();
		String query = "꼬따오";
		JSONArray tweets = twitter.runSearchingKeyword(query, sinceId);
		if(tweets.size() != 0)
			sinceId = (long) ((JSONObject) tweets.get(0)).get("id");

		for(int i=0;i<tweets.size();i++) {
			JSONObject tweet = (JSONObject) tweets.get(i);
			System.out.println("@"+tweet.get("id")+" - "+tweet.get("text"));
		}
		System.out.println("tweets length: " + tweets.size());
		System.out.println("Next SinceId: " + sinceId);
	}
	
	public static void koalaTest() {
		System.out.println("Hello Koala");
		String src = "Hello I'm fine thank you and you 나무에 친구가 올라가다.";
		List<String> dataList = new ArrayList<String>();
		String result = "";
		
		Tagger tagger = new Tagger();
		
		List<Sentence> sentences = tagger.jTag(src);
		
		for(int i=0;i<sentences.size();i++) {
			Sentence tmp = sentences.get(i);
			
			List<Word> words = tmp.jNouns();
	
			for(int j=0;j<words.size();j++) {
				Word word = words.get(j);
				String strWord1 = word.toString();
				String strWord2 = strWord1.split(" ")[1];
				String[] strWord3 = strWord2.split("\\)");

				for(int k=0;k<strWord3.length;k++) {
					String[] chkS = strWord3[k].split("/");
					if(chkS[1].equals("NNG(Noun")) {
						dataList.add(chkS[0]);
					}
				}
			}
		}
		
         for (int i = 0; i < dataList.size(); i++) {
             if (!result.contains(dataList.get(i))) {
                 result += dataList.get(i) + " ";
             }
         }

         System.out.println(result);
	}
	
	public static void mysqlTest() {
		System.out.println("MySQL Test");
		MySQLConn mysql = new MySQLConn();
		System.out.println("keyword: "+mysql.getKeyword());
	}
	
	public static void propertiesTest() {
		DBProperties dbp = new DBProperties();
		dbp.setProperties();
		Properties prop = dbp.getProperties();
		System.out.println("URL: "+prop.getProperty("URL"));
		System.out.println("USERNAME: "+prop.getProperty("USERNAME"));
		System.out.println("PASSWORD: "+prop.getProperty("PASSWORD"));
	}
	
	public static void main(String[] args) {
		koalaTest();
		//twitterTest();
		//mysqlTest();
		//propertiesTest();
	}
}
