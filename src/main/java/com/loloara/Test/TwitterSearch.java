package com.loloara.Test;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class TwitterSearch {
	private ConfigurationBuilder cb;
	
	public TwitterSearch() {
		cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
			.setOAuthConsumerKey("HaUrIT4TqjgvATyc2SLRt9A13")
			.setOAuthConsumerSecret("DlexSCGcSMKM96lwCmHys0h9XxFqIBsRdKEOnwkOd06a0OWj5r")
			.setOAuthAccessToken("119334167-rDMB1jSQOtELPkG2H86c7A04tkCpqTDC0r02PTbB")
			.setOAuthAccessTokenSecret("uJUnRIl23dMS8xIPDxYtG10nPgywboUY7a2Rgpch8jAZ7");
	}
	
	public JSONArray runSearchingKeyword(String q, long sinceId) {
		Twitter twitter = new TwitterFactory(cb.build()).getInstance();		
		JSONArray tweets = new JSONArray();
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String since = sdf.format(date);
		
		try {
			Query query = new Query(q);
			query.setResultType(Query.RECENT);
			query.setCount(100);
			query.setMaxId(Long.MAX_VALUE);
			query.setSince(since);
			QueryResult result;
			
			do {
				query.setSinceId(sinceId);
				if(tweets.size() != 0) {
					JSONObject lastObj = (JSONObject) tweets.get(tweets.size()-1);
					long lastId = (long) lastObj.get("id");;
					query.setMaxId(lastId - 1L);
				}
				result = twitter.search(query);
				for(int i=0;i<result.getTweets().size();i++) {
					List<Status> tweetList = result.getTweets();
					Status tweetStatus = tweetList.get(i);
					JSONObject tweet = new JSONObject();
					
					tweet.put("id", tweetStatus.getId());
					tweet.put("text", tweetStatus.getText());
					tweets.add(tweet);
				}
			}while(query.getSinceId() < query.getMaxId() && result.hasNext());
			
		} catch (TwitterException e) {
			e.printStackTrace();
			System.out.println("Failed to search tweets: " + e.getMessage());
			
			return tweets;
		}
		return tweets;
	}
}