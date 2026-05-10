package ma.abdelali.digitalbanking.services;

import ma.abdelali.digitalbanking.dtos.ChatRequest;
import ma.abdelali.digitalbanking.dtos.ChatResponse;

public interface ChatbotService {

    ChatResponse chat(ChatRequest request);
    void ingestDocumentation(String documentation);
}
