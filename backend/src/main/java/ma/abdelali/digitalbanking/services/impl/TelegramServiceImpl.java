package ma.abdelali.digitalbanking.services.impl;

import lombok.extern.slf4j.Slf4j;
import ma.abdelali.digitalbanking.services.TelegramService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class TelegramServiceImpl implements TelegramService {

    @Value("${TELEGRAM_BOT_TOKEN:}")
    private String botToken;

    @Value("${TELEGRAM_CHAT_ID:}")
    private String chatId;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void sendMessage(String message) {
        if (botToken == null || botToken.isEmpty() || chatId == null || chatId.isEmpty()) {
            log.warn("Telegram bot token or chat ID is not configured. Notification skipped.");
            return;
        }

        try {
            String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";
            
            Map<String, String> body = new HashMap<>();
            body.put("chat_id", chatId);
            body.put("text", message);
            body.put("parse_mode", "HTML");

            restTemplate.postForObject(url, body, String.class);
            log.info("Telegram notification sent successfully to chat ID: {}", chatId);
        } catch (Exception e) {
            log.error("Failed to send Telegram notification: {}", e.getMessage());
        }
    }
}
