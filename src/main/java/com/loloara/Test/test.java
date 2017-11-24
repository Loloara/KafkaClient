package com.loloara.Test;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class test {
	private static long sinceId = 933225613778812929L;
	public static void main(String[] args) {
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
}
