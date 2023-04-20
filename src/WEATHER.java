import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

public class WEATHER {
	HashMap	<String, String> maps = new HashMap<>();
	String ServiceKey = "YOUR_OPEN-API_KEY";

	void Getweather() {
        try {
        	String nx = "61";
        	String ny = "128";
        	String baseDate = "20230412";
        	String baseTime = "1100";
        	
        	String urlStr = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?serviceKey=" + ServiceKey + "&dataType=JSON&base_date=" +baseDate + "&base_time=" + baseTime + "&nx=" + nx + "&ny=" + ny;
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-type", "application/json");
        	MANAGER.Logwriter("WEATHER::Getweather", "Response code: " + conn.getResponseCode());

			BufferedReader rd;
			boolean responses_200 = false;
			if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
			    rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			    responses_200 = true;
			} else {
			    rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
			    sb.append(line);
			}
			rd.close();
			conn.disconnect();
			
			if(responses_200) {
				JSONParser parser = new JSONParser();
				JSONObject response = (JSONObject) parser.parse(sb.toString());
				JSONObject parse_response = (JSONObject) response.get("response");
				JSONObject parse_header = (JSONObject) parse_response.get("header");
				String errCode = (String)parse_header.get("resultCode");
				if(errCode.compareTo("00") != 0) {
		        	MANAGER.Logwriter("WEATHER::Getweather", "Error Occure : ("+ errCode + ") " + (String)parse_header.get("resultMsg"));
		        	return;
				}
				JSONObject parse_body = (JSONObject) parse_response.get("body");
				JSONObject parse_items = (JSONObject) parse_body.get("items");
				JSONArray parse_item = (JSONArray) parse_items.get("item");
				for(int i = 0; i < parse_item.size(); i++) {
					JSONObject obj = (JSONObject)parse_item.get(i);
					maps.put((String)obj.get("category"), (String)obj.get("fcstValue"));
				}
			}
			
			System.out.println(sb.toString());
        	MANAGER.Logwriter("WEATHER::Getweather", "Load Complete");
    	}
    	catch(Exception e){
    		MANAGER.Logwriter("WEATHER::Getweather", "<Exception> " + e);
    		System.out.println(e);
    	}
    }

	void GetAirQuality() {
        try {
        	String sidoName = "서울";
        	String cityName = "노원구";

        	String urlStr = "http://apis.data.go.kr/B552584/ArpltnStatsSvc/getCtprvnMesureSidoLIst?serviceKey=" + ServiceKey + "&returnType=JSON&sidoName=" + URLEncoder.encode(sidoName,"UTF-8") + "&searchCondition=HOUR";
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-type", "application/json");
        	MANAGER.Logwriter("WEATHER::GetAirQuality", "Response code: " + conn.getResponseCode());

			BufferedReader rd;
			boolean responses_200 = false;
			if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
			    rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			    responses_200 = true;
			} else {
			    rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));	        	
			}
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
			    sb.append(line);
			}
			rd.close();
			conn.disconnect();
			
			if(responses_200) {
				JSONParser parser = new JSONParser();
				JSONObject response = (JSONObject) parser.parse(sb.toString());
				JSONObject parse_response = (JSONObject) response.get("response");
				JSONObject parse_header = (JSONObject) parse_response.get("header");
				String errCode = (String)parse_header.get("resultCode");
				if(errCode.compareTo("00") != 0) {
		        	MANAGER.Logwriter("WEATHER::GetAirQuality", "Error Occure : ("+ errCode + ") " + (String)parse_header.get("resultMsg"));
		        	return;
				}
				JSONObject parse_body = (JSONObject) parse_response.get("body");
				JSONArray parse_item = (JSONArray) parse_body.get("items");
				
				for(int i = 0; i < parse_item.size(); i++) {
					JSONObject obj = (JSONObject)parse_item.get(i);
					//If cityName is Different, Skip
					if(cityName.compareTo((String)obj.get("cityName")) != 0) continue;
					maps.put("pm10Value", (String)obj.get("pm10Value"));
					maps.put("pm25Value", (String)obj.get("pm25Value"));
					maps.put("o3Value", (String)obj.get("o3Value"));
					maps.put("coValue", (String)obj.get("coValue"));
					maps.put("no2Value", (String)obj.get("no2Value"));
					maps.put("so2Value", (String)obj.get("so2Value"));
					maps.put("airQuality_time", (String)obj.get("dataTime"));
				}
			}
			System.out.println(sb.toString());
        	MANAGER.Logwriter("WEATHER::GetAirQuality", "Load Complete");
    	}
    	catch(Exception e){
    		MANAGER.Logwriter("WEATHER::GetAirQuality", "<Exception> " + e);
    		System.out.println(e);
    	}
    }
	
	void Printweather() {
		  for (Entry<String, String> entrySet : maps.entrySet()) {
	    		MANAGER.Logwriter("WEATHER::Printweather", entrySet.getKey() + " : " + entrySet.getValue());
	      }
	}

	void Upload() {
		final String ADDR = ""; // Ex)192.168.0.122:3306/NOTICE
		final String ID = ""; // Insert your database account
		final String PASSWORD = "";
		
		try {
//			Class.forName("com.mysql.jdbc.Driver");
//			Connection conn = DriverManager.getConnection("jdbc:mysql://" + ADDR, ID, PASSWORD);

//			java.sql.Statement stmt = conn.createStatement();
//			stmt.execute("TRUNCATE TABLE weather");
//			stmt.close();
			if(maps.isEmpty()) {
				MANAGER.Logwriter("WEATHER::Upload", "There are no item");
				return;
			}
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("REPLACE INTO weather(");
			for (Entry<String, String> entrySet : maps.entrySet()) {
	            stringBuilder.append(entrySet.getKey() + ", ");
			}
			stringBuilder.setLength(stringBuilder.length() - 2);
			stringBuilder.append(") VALUES (");
			for (Entry<String, String> entrySet : maps.entrySet()) {
	            stringBuilder.append("'" + entrySet.getValue() + "', ");
			}
			stringBuilder.setLength(stringBuilder.length() - 2);
			stringBuilder.append(");");
			
			String sqlQuery = stringBuilder.toString();

			MANAGER.Logwriter("WEATHER::Upload", "Complete sql upload.");
//			if (pstmt != null && !pstmt.isClosed())
//				pstmt.close();
//		} catch (ClassNotFoundException e) {
//			MANAGER.Logwriter("WEATHER::Upload", "<ClassNotFoundException>");
//			System.out.println(e);
//		} catch (SQLException e) {
//			MANAGER.Logwriter("WEATHER::Upload", "<SQLException>");
//			System.out.println(e);
		} catch (Exception e) {
			MANAGER.Logwriter("WEATHER::Upload", "<Exception>");
			System.out.println(e);
		}
	}
}
