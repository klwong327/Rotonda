import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Traffic {

	public static String idToName(String id) {
    	if (id.contains("EH")) {
    		return "Eastern Harbour Crossing";
    	}
    	else if (id.contains("WH")) {
    		return "Western Harbour Crossing";
    	}
    	else if (id.contains("CH")) {
    		return "Cross Harbour Tunnel";
    	}
		return null;
    }
    
    public static String idToDirection(String id) {
    	if (id.contains("K03")) {
    		return "Waterloo Road (Southbound)";
    	}
    	else if (id.contains("H2")) {
    		return "Canal Road Flyover (Northbound)";
    	}
		return null;
    }
    
    public static String idToColor(String id) {
    	if (id.contains("1")) {
    		return "Congested traffic now";
    	}
    	else if (id.contains("2")) {
    		return "Slow traffic now";
    	}
    	else if (id.contains("3")) {
    		return "Smooth traffic now";
    	}
		return null;
    }

    public static String getTraffic(String TunnelIDfromBot , String LocationIDfromBot) throws IOException, ParseException {
        URL url = new URL( "https://api.factmaven.com/xml-to-json/?xml=https://resource.data.one.gov.hk/td/journeytime.xml");

        Scanner in = new Scanner((InputStream) url.getContent());
        
        String result="";
        while (in.hasNext()){
            result+=in.nextLine();
        }
       
        JSONObject object = new JSONObject(result);
        JSONObject main = object.getJSONObject("jtis_journey_list");
        JSONArray main1 = main.getJSONArray("jtis_journey_time");
        for(int i = 0; i < main1.length(); i++){
        	JSONObject obj = main1.getJSONObject(i);
        	String loc = obj.getString("LOCATION_ID");
        	String tunnel = obj.getString("DESTINATION_ID");
        	String time = obj.getString("JOURNEY_DATA");
        	String date = obj.getString("CAPTURE_DATE");
        	String color = obj.getString("COLOUR_ID");
        	
        	SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        	String dateStriped = date.replaceAll("T"," ").replaceAll("-", "/");
        	java.util.Date dateFromData = format.parse(dateStriped);
			
        	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        	LocalDateTime now = LocalDateTime.now(); 
        	String currentDate = dtf.format(now);
        	java.util.Date dateForNow = format.parse(currentDate);

        	long difference = dateForNow.getTime() - dateFromData.getTime(); 
        	long diffMinutes = difference / (60 * 1000) % 60;
        	date = date.replaceAll("T"," (").replaceAll("-", "/") + ")";
        	currentDate = currentDate.substring(0, 11) + "(" + currentDate.substring(11) + ")";      	
        	
        	if(loc.equals(LocationIDfromBot)&tunnel.equals(TunnelIDfromBot)) {
        		return ("Capture Date: " + date
        				+ "\nCurrent Time: " + currentDate
        				+ "\nUpdate from: " + diffMinutes + " minutes before"+
        				"\n"+"From: "+Traffic.idToDirection(LocationIDfromBot)+
        				"\n" +"To: "+Traffic.idToName(TunnelIDfromBot)+
        				"\n"+"*Average Journey Time:* "+time+" minutes"+
        				"\n"+"*Description:* "+idToColor(color));	
        	}
        }
		return "Information can not be found. Please try again later.";
    }
}
