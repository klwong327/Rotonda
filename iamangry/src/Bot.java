import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Random;

public class Bot extends TelegramLongPollingBot  {

	public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi( );
        try{
            telegramBotsApi.registerBot(new Bot());
        }
        catch(TelegramApiException e) {
        e.printStackTrace();
        }
    }

    public void sendMsg(Message message, String text){
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try{
            sendMessage(sendMessage);
        }
        catch (TelegramApiException e){
            e.printStackTrace();
        }
    }
    
    public String idTOcctv(String tunnel, String location) {
		if(tunnel.contains("EH")) {
			if(location.contains("K03")) {
				return "K952F";
			}else if(location.contains("H2")) {
				return "AID04222";
			}
		}else if(tunnel.contains("CH")) {
			if(location.contains("K03")) {
				return "K107F";
			}else if(location.contains("H2")) {
				return "H207F";
			}
		}else if (tunnel.contains("WH")) {
			if(location.contains("K03")) {
				return "K901F";
			}else if(location.contains("H2")) {
				return "H702F";
			}
		}
		return null;
    }
    
    public static String botTunnelID;
    public static String botLocationID;
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
        	Message message = update.getMessage();
        	long chat_id = update.getMessage().getChatId();
        	
        	
        	switch (message.getText()){
            case "/start":
                sendMsg(message,"Hello! I am Rotonda which can provide real-time traffic "
                		+ "information of 3 major cross-harbour tunnel for you. "
                		+ "You can type the tunnel name first, which are ．eastern・, "
                		+ "．western・, or ．hunghom・. Then, followed by typing the "
                		+ "direction of destination, ．south・ or ．north・. "
                		+ "With two inputs, I will show the latest updates for you!");
                break;
           
            case "/whoareyou":
                sendMsg(message,"Hello! I am Rotonda which can provide real-time traffic "
                		+ "information of 3 major cross-harbour tunnel in Hong Kong for you.");
                break;
           
            case "/howtouse":
                sendMsg(message,"1.	Input the tunnel name among the 3 major cross-harbour tunnel."
                		+ "(i.e. eastern, western, hunghom)\r\n" + 
                		"2.	Input the direction of destination. (i.e. south, north)");
                break;
                
            case "/howareyou":
                sendMsg(message,"Great! How about you?");
                break;
            
                
            default:
			String keyword = message.getText().toString().toLowerCase().replaceAll("\\s+","");
				if (keywordIsValid(keyword)) {
					if (nameIsValid(keyword)) {
						sendMsg(message,"Please input direction (South/North):");
						String tunnelID = nameToID(keyword);
						Bot.botTunnelID = tunnelID;
					}
					else if(directionIsValid(keyword)) {
						String locationID = directionToID(keyword);
						Bot.botLocationID = locationID;
						Random rand = new Random();
						SendPhoto msg = new SendPhoto()
								.setChatId(chat_id)
								.setPhoto("https://tdcctv.data.one.gov.hk/"+
								idTOcctv(Bot.botTunnelID, Bot.botLocationID)+".JPG?v="+rand.nextInt(99));
		              
						try {
							sendMsg(message,Traffic.getTraffic(Bot.botTunnelID,Bot.botLocationID));
							sendPhoto(msg);	
							} catch (IOException e) {
									e.printStackTrace();
								} catch (TelegramApiException e) {
									e.printStackTrace();
								} catch (ParseException e) {
									e.printStackTrace();
								}
					}				
				}
				else {
					sendMsg(message,"Please input 'eastern' OR 'western' OR 'hunghom'");
				}							  
        	}
        }
    }
            
    public boolean keywordIsValid(String keyword){
    	if (keyword.contains("eastern") || keyword.contains("western") || keyword.contains("hunghom") 
    			|| keyword.contains("south") || keyword.contains("north"))
    		return true;
    	else
    		return false;
    }
    
    public boolean nameIsValid(String tunnelName){
    	if (tunnelName.contains("eastern") || tunnelName.contains("western") || tunnelName.contains("hunghom"))
    		return true;
    	else
    		return false;
    }
    
    public boolean directionIsValid(String direction){
    	if (direction.contains("south") || direction.contains("north") )
    		return true;
    	else
    		return false;
    }
    
	public String nameToID(String tunnelName) {
    	if (tunnelName.contains("eastern")) {
    		return "EH";
    	}
    	else if (tunnelName.contains("western")) {
    		return "WH";
    	}
    	else if (tunnelName.contains("hunghom")) {
    		return "CH";
    	}
		return null;
    }
    
    public String directionToID(String direction) {
    	if (direction.contains("south")) {
    		return "K03";
    	}
    	else if (direction.contains("north")) {
    		return "H2";
    	}
		return null;
    }
    
    public String getBotUsername() {
        return "Rotonda_trafficbot";
    }

    public String getBotToken() {
        return "token" ;
    }
}
