package com.loloara.Test;

import java.util.ArrayList;
import java.util.List;
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
		String query = "#twice";
		JSONArray tweets = twitter.runSearchingKeyword(query, sinceId);
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
		String src = "폭력은 더 큰 폭력을 낳습니다. 그리고 외면은 폭력에게 더 큰 정당성을 부여합니다. 나는 ‘페미니스트’가 아닌 ‘조직폭력배’와 싸우고 있습니다. 시비를 조장하고 논란을 키우는 자들에게는 이것이 정당한 경쟁으로 보이는 모양입니다. 이 논란은 ‘익명’의 집단이 ‘실명’의 개인에게 가하는 명백한 ‘폭력’입니다. 저들의 언어의 폭력성이 증명하죠. 그리고 저는 손잡이가 없는 칼날과 싸울 도리가 없습니다. 몇몇 온라인 커뮤니티를 거점 삼아 하루종일 무리 지어 몰려다니며 쏟아내는 인신공격은 인권 운동이자 세상에 대한 피해자들의 분노로 조작되고 둔갑하여 세상을 어지럽히고 있습니다. SNS를 통한 저의 외침은 세속적 가치를 내려 놓고 진정한 나의 가치와 관계를 찾고자 하는 의지입니다. 저의 노력이 언제나 처럼 폭도들에 의해 ‘인생의 낭비’로 조롱 당하고 매도 당한다 해도 저는 지금의 인생을 온 힘을 다해 성실하게 살아나가고자 합니다. 부끄럽지 않고 진실되게 살아가고 싶습니다.";
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
		//koalaTest();
		//twitterTest();
		//mysqlTest();
		propertiesTest();
	}
}
