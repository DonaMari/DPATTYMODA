package com.dpatty.dto.message;

import com.dpatty.model.Message;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageResponse {
    private Long id;
    private Long senderId;
    private String senderName;
    private Long recipientId;
    private String recipientName;
    private String content;
    private Boolean isRead;
    private Message.MessageType messageType;
    private LocalDateTime createdAt;
}