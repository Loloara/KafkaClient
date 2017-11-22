package com.loloara.Test;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.List;

public class TwitterSearch_backup {
	private ConfigurationBuilder cb;
	
	public TwitterSearch_backup() {
		cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
			.setOAuthConsumerKey("HaUrIT4TqjgvATyc2SLRt9A13")
			.setOAuthConsumerSecret("DlexSCGcSMKM96lwCmHys0h9XxFqIBsRdKEOnwkOd06a0OWj5r")
			.setOAuthAccessToken("119334167-rDMB1jSQOtELPkG2H86c7A04tkCpqTDC0r02PTbB")
			.setOAuthAccessTokenSecret("uJUnRIl23dMS8xIPDxYtG10nPgywboUY7a2Rgpch8jAZ7");
	}
	
	public List<Status> runSearchingKeyword(String q, long sinceId) {
		Twitter twitter = new TwitterFactory(cb.build()).getInstance();		
		List<Status> tweets = new ArrayList<>();
		try {
			Query query = new Query(q);
			query.setResultType(Query.RECENT);
			query.setCount(100);
			query.setMaxId(Long.MAX_VALUE);
			QueryResult result;
			do {
				query.setSinceId(sinceId);
				if(tweets.size() != 0)
					query.setMaxId(tweets.get(tweets.size()-1).getId()-1L);
				result = twitter.search(query);
				tweets.addAll(result.getTweets());
			}while(query.getSinceId() < query.getMaxId() && result.hasNext());
		} catch (TwitterException e) {
			e.printStackTrace();
			System.out.println("Failed to search tweets: " + e.getMessage());
			if(tweets.size() == 0)
				System.out.println("has no tweets");
			return tweets;
		}
		return tweets;
	}
}