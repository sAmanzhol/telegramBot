import org.json.JSONObject;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class xanTestBot extends TelegramLongPollingBot {

    private static String streamToString(InputStream inputStream) {
        String text = new Scanner(inputStream, "UTF-8").useDelimiter("\\Z").next();
        return text;
    }

    public static String jsonGetRequest(String urlQueryString) {
        String json = null;
        try {
            URL url = new URL(urlQueryString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.connect();
            InputStream inStream = connection.getInputStream();
            json = streamToString(inStream); // input stream to string
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

    public void onUpdateReceived(Update update) {
        SendMessage message = new SendMessage();
        String command = update.getMessage().getText();
        if(command.equals("/myname")){
            message.setText(update.getMessage().getFrom().getFirstName());
            message.setChatId(update.getMessage().getChatId());
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        if(command.equals("/report")){
            System.out.println("");
            JSONObject json = new JSONObject(jsonGetRequest("https://blockchain.info/ticker"));
            JSONObject currency = json.getJSONObject("USD");
            double currentCurrency = currency.getDouble("15m");

            message.setText("Current currency is: " + currentCurrency + "$");
            message.setChatId(update.getMessage().getChatId());
            ScheduledExecutorService execService = Executors.newScheduledThreadPool(5);

            execService.scheduleAtFixedRate(()->{
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }, 0, TimeUnit.MINUTES.toMillis(60), TimeUnit.MILLISECONDS);

        }

    }

    public String getBotUsername() {
        return "xanTestBot";
    }

    public String getBotToken() {
        return "525313729:AAGzeAi0EzehFtJ7vg_hKbQRRAyLRlI_tvo";
    }

}
