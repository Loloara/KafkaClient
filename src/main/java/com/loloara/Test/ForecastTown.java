package com.loloara.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class ForecastTown{
	 Logger log = LoggerFactory.getLogger(ForecastTown.class);
	 String x = "58";
	 String y = "126"; //염창동
	 
    //API 요청 주소
    private String forecastTownApi_1 = "http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastGrib?"
	+ "serviceKey=8SztTtcovPf76Y47K7oMCI4Sfoc7xPCmdMhxn1agLuIm9YcJcJ1OyRm525uDAgBY7ivR0%2FdXvx4wOFGZUtVPFA%3D%3D&";
	private String forecastTownApi_2 = "base_date=%s&base_time=%s&nx=%s&ny=%s&numOfRows=10&pageSize=10&pageNo=1&startPage=1&_type=json";
	//base_date = today, base_time = xx40, nx, ny = 염창동, numOfRows, pageSize = 속성 10개, type = json
    
	public ForecastTown(){}
	public ForecastTown(String x, String y){
		this.x = x;
		this.y = y;
	}
    
    public JSONArray getTownForecastFromJSON() {         
    	JSONArray result = new JSONArray();
        
        //요청할 데이터의 기준 날짜 생성 
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
    	
        Calendar baseDate = Calendar.getInstance();
        String baseString = sdf.format(baseDate.getTime());
        String strBaseDate = baseString.substring(0, 8);
        String strBaseTime = baseString.substring(8);
        int intBaseTime = Integer.parseInt(strBaseTime);
        
        if((intBaseTime % 100) <= 20) {
        	intBaseTime = (((intBaseTime / 100) - 1)* 100 + intBaseTime % 100);
        	strBaseTime = String.valueOf(intBaseTime);
        	if(intBaseTime < 1000)
        		strBaseTime = "0"+strBaseTime;
        	System.out.println("기상청 날씨가 아직 업데이트 되지 않아 1시간 전 날씨를 가져옵니다.");
        }
        
        //strBaseDate = "20171101"
        //strBaseTime = "0640"
        
        String url = forecastTownApi_1 + String.format(forecastTownApi_2, strBaseDate,strBaseTime,x,y);
        System.out.println("url :" + url);    

        //API주소로 요청
        JSONObject responseObj = (JSONObject) getRemoteJSON(url).get("response"); 

        
        JSONObject headerObj = (JSONObject) responseObj.get("header");
        String resultCode = (String) headerObj.get("resultCode");
        if(headerObj != null && resultCode.equals("0000")) {
        	System.out.println(headerObj.get("resultMsg"));
        	JSONObject bodyObj = (JSONObject) responseObj.get("body");
        	int totCnt = Integer.parseInt(bodyObj.get("totalCount").toString());
            if(totCnt==0) {
            	System.out.println("totalCount: 0");
            	System.out.println(result);
            	return result;                 
            }
            result = (JSONArray)((JSONObject) bodyObj.get("items")).get("item");
    	}
         return result;
     }
    
    private JSONObject getRemoteJSON(String url) {
        
        StringBuffer jsonHtml = new StringBuffer();
        
        
        try {            
            URL u = new URL(url);            
            InputStream uis = u.openStream();
            
            BufferedReader br =    new BufferedReader(new InputStreamReader(uis,"UTF-8"));
                    
            String line = null;
            while ((line = br.readLine())!= null){
                    jsonHtml.append(line + "\n");
            }
            br.close();
            uis.close();        
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonObj = (JSONObject) JSONValue.parse(jsonHtml.toString());  
        return jsonObj;
    }
}