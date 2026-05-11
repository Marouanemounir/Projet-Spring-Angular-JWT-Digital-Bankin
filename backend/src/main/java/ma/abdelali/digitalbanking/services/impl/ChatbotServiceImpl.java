package ma.abdelali.digitalbanking.services.impl;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.abdelali.digitalbanking.dtos.ChatRequest;
import ma.abdelali.digitalbanking.dtos.ChatResponse;
import ma.abdelali.digitalbanking.services.ChatbotService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotServiceImpl implements ChatbotService {

    @Value("${OPENAI_API_KEY:}")
    private String openaiApiKey;

    private OpenAiService openAiService;

    private final List<String> knowledgeBase = new ArrayList<>();
    private final Map<String, List<ChatMessage>> conversationHistories = new HashMap<>();

    @PostConstruct
    public void init() {
        if (openaiApiKey != null && !openaiApiKey.trim().isEmpty()) {
            this.openAiService = new OpenAiService(openaiApiKey);
            log.info("OpenAI Service initialized successfully.");
        } else {
            log.warn("OPENAI_API_KEY is not set. Chatbot will use fallback rules.");
        }
    }

    @Override
    public ChatResponse chat(ChatRequest request) {
        String conversationId = request.getConversationId() != null ? 
            request.getConversationId() : UUID.randomUUID().toString();

        String answer;
        List<String> sources = retrieveRelevantSources(request.getMessage());

        if (openAiService != null) {
            answer = generateOpenAiAnswer(conversationId, request.getMessage());
        } else {
            answer = generateAnswer(request.getMessage());
        }

        log.info("Chat interaction - Conversation: {}, Message: {}", conversationId, request.getMessage());

        return ChatResponse.builder()
                .answer(answer)
                .sources(sources)
                .conversationId(conversationId)
                .build();
    }

    private String generateOpenAiAnswer(String conversationId, String message) {
        List<ChatMessage> history = conversationHistories.computeIfAbsent(conversationId, k -> {
            List<ChatMessage> initHistory = new ArrayList<>();
            initHistory.add(new ChatMessage("system", "You are an AI assistant for a Digital Banking application. You are helpful, professional, and concise. You help customers manage their accounts, perform transactions, and answer questions about the bank."));
            return initHistory;
        });

        history.add(new ChatMessage("user", message));

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(history)
                .temperature(0.7)
                .build();

        try {
            String answer = openAiService.createChatCompletion(chatCompletionRequest)
                    .getChoices().get(0).getMessage().getContent();
            history.add(new ChatMessage("assistant", answer));
            return answer;
        } catch (Exception e) {
            log.error("Failed to call OpenAI API", e);
            return "Sorry, I am currently unable to process your request using AI. " + generateAnswer(message);
        }
    }

    @Override
    public void ingestDocumentation(String documentation) {
        if (documentation != null && !documentation.isEmpty()) {
            // Split documentation into chunks
            String[] chunks = documentation.split("\n\n");
            for (String chunk : chunks) {
                if (!chunk.isEmpty()) {
                    knowledgeBase.add(chunk.trim());
                }
            }
            log.info("Documentation ingested. Total knowledge base chunks: {}", knowledgeBase.size());
        }
    }

    private String generateAnswer(String message) {
        // Fallback answers for common questions
        String lowerMessage = message.toLowerCase();

        if (lowerMessage.contains("customer") && (lowerMessage.contains("create") || lowerMessage.contains("add"))) {
            return "To create a new customer, use the Customers section and click 'Add Customer'. " +
                    "Fill in the customer name and email address, then click Submit. " +
                    "Once created, you can add bank accounts for this customer.";
        }

        if (lowerMessage.contains("account") && (lowerMessage.contains("create") || lowerMessage.contains("add"))) {
            return "To create a bank account, navigate to Accounts section and click 'Create Account'. " +
                    "Choose between Current Account (with overdraft) or Saving Account (with interest rate). " +
                    "Select a customer and set the initial balance and other parameters.";
        }

        if (lowerMessage.contains("debit") || lowerMessage.contains("withdraw")) {
            return "Debit operations reduce the account balance. Go to Account Details, click 'Debit', " +
                    "enter the amount and description. For Saving Accounts, the balance cannot go negative. " +
                    "For Current Accounts, you can use the overdraft facility up to the limit.";
        }

        if (lowerMessage.contains("credit") || lowerMessage.contains("deposit")) {
            return "Credit operations increase the account balance. Go to Account Details, click 'Credit', " +
                    "enter the amount and description. There are no restrictions on credit operations.";
        }

        if (lowerMessage.contains("transfer")) {
            return "To transfer money between accounts, go to Account Details and click 'Transfer'. " +
                    "Select the source account and destination account, enter the amount, and confirm. " +
                    "Both accounts must be activated for transfer to work.";
        }

        if (lowerMessage.contains("dashboard")) {
            return "The Dashboard shows real-time statistics including total customers, accounts, balance, " +
                    "and transaction summaries. Charts display account types and statuses distribution.";
        }

        if (lowerMessage.contains("password") || lowerMessage.contains("change")) {
            return "To change your password, go to Account Settings and click 'Change Password'. " +
                    "Enter your old password and new password (minimum 6 characters). Click 'Update' to confirm.";
        }

        if (lowerMessage.contains("role") || lowerMessage.contains("permission")) {
            return "The system has three roles: ADMIN (full access), MANAGER (manage accounts and operations), " +
                    "and USER (view and manage personal operations). Contact an admin to change your role.";
        }

        // Default response
        return "I'm a Banking Assistant. I can help you with questions about: " +
                "creating customers, managing accounts, performing transactions (debit/credit/transfer), " +
                "viewing dashboard, and account operations. Ask me anything about the Digital Banking application!";
    }

    private List<String> retrieveRelevantSources(String message) {
        List<String> sources = new ArrayList<>();
        
        // Simple keyword matching to return relevant sources
        String lowerMessage = message.toLowerCase();
        
        if (lowerMessage.contains("documentation")) {
            sources.add("Application Documentation");
        }
        if (lowerMessage.contains("faq") || lowerMessage.contains("frequently")) {
            sources.add("FAQ Section");
        }
        if (lowerMessage.contains("banking") || lowerMessage.contains("account")) {
            sources.add("Banking Guide");
        }
        
        if (sources.isEmpty()) {
            sources.add("General Knowledge Base");
        }
        
        return sources;
    }
}
