package com.loloara.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import kr.bydelta.koala.data.Sentence;
import kr.bydelta.koala.data.Word;
import kr.bydelta.koala.twt.Tagger;

public class test {
	private static long sinceId = 0L;
	public static void twitterTest() {
		TwitterSearch twitter = new TwitterSearch();
		String query = "아이유";
		JSONArray tweets = twitter.runSearchingKeyword(query, sinceId);
		Date sinceDate = new Date();
		Date latelyDate = null;
		
		if(tweets.size() != 0) {
			sinceId = (long) ((JSONObject) tweets.get(0)).get("id");
			latelyDate = (Date) ((JSONObject) tweets.get(0)).get("date");
			sinceDate = (Date) ((JSONObject) tweets.get(tweets.size()-1)).get("date");
		}

		for(int i=0;i<tweets.size();i++) {
			JSONObject tweet = (JSONObject) tweets.get(i);
			Date tmp = (Date) tweet.get("date");
			System.out.println("@"+tweet.get("id")+ "::" + tmp +" - "+tweet.get("text"));
		}
		
		System.out.println("Since: " + sinceDate);
		System.out.println("Lately: " + latelyDate);
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
		System.out.println("sinceDate: "+mysql.getKeyword()[3]);
		SimpleDateFormat sdf= new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",Locale.US);
		String str_since = "Fri Dec 08 09:00:07 KST 2017";
		String str_lately = "Fri Dec 08 10:00:07 KST 2017";
		Date sinceDate = null;
		Date latelyDate = null;
		try {
			sinceDate = sdf.parse(str_since);
			latelyDate = sdf.parse(str_lately);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//mysql.updateKeywordHistory(100, 3, 284719823, sinceDate, latelyDate);
	}
	
	public static void calanderTest() {
		Calendar date1 = new GregorianCalendar();
		Calendar date2 = new GregorianCalendar();
		SimpleDateFormat sdf= new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",Locale.US);
		try {
			date1.setTime(sdf.parse("Thu Dec 07 11:22:33 KST 2017"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		System.out.println("date1: " + date1.getTime());
		System.out.println("date2: " + date2.getTime());
		
		int com  = date1.compareTo(date2);
		if(com == 1) {
			System.out.println("date1이 더 최근");
		}else if(com == -1){
			System.out.println("date1이 더 오래됨");
		}else {
			System.out.println("같다");
		}
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
		//koalaTest();
		twitterTest();
		//mysqlTest();
		//propertiesTest();
		//calanderTest();
	}
}
