package ma.abdelali.digitalbanking.dtos;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRequest {
    private String message;
    private String conversationId;
}
