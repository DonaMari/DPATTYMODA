package com.dpatty.service;

import com.dpatty.dto.message.CreateMessageRequest;
import com.dpatty.dto.message.MessageResponse;
import com.dpatty.model.Message;
import com.dpatty.model.User;
import com.dpatty.repository.MessageRepository;
import com.dpatty.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthService authService;

    @Autowired
    private NotificationService notificationService;

    public Page<MessageResponse> getUserMessages(Pageable pageable) {
        User currentUser = authService.getCurrentUser();
        Page<Message> messages = messageRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId(), pageable);
        return messages.map(message -> modelMapper.map(message, MessageResponse.class));
    }

    public List<MessageResponse> getConversation(Long otherUserId) {
        User currentUser = authService.getCurrentUser();
        List<Message> messages = messageRepository.findConversation(currentUser.getId(), otherUserId);
        return messages.stream()
                .map(message -> modelMapper.map(message, MessageResponse.class))
                .toList();
    }

    @Transactional
    public MessageResponse sendMessage(CreateMessageRequest request) {
        User currentUser = authService.getCurrentUser();
        
        User recipient = userRepository.findById(request.getRecipientId())
                .orElseThrow(() -> new RuntimeException("Destinatario no encontrado"));
        
        Message message = new Message();
        message.setSender(currentUser);
        message.setRecipient(recipient);
        message.setContent(request.getContent());
        message.setMessageType(Message.MessageType.CHAT);
        message.setIsRead(false);
        
        Message savedMessage = messageRepository.save(message);
        
        // Send notification to recipient
        notificationService.createNotification(
            recipient.getId(),
            "Nuevo Mensaje",
            "Tienes un nuevo mensaje de " + currentUser.getFullName(),
            "INFO"
        );
        
        return modelMapper.map(savedMessage, MessageResponse.class);
    }

    @Transactional
    public void markAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado"));
        
        User currentUser = authService.getCurrentUser();
        if (!message.getRecipient().getId().equals(currentUser.getId())) {
            throw new RuntimeException("No autorizado para marcar este mensaje");
        }
        
        message.setIsRead(true);
        messageRepository.save(message);
    }

    @Transactional
    public void markConversationAsRead(Long otherUserId) {
        User currentUser = authService.getCurrentUser();
        List<Message> unreadMessages = messageRepository.findUnreadMessages(currentUser.getId());
        
        for (Message message : unreadMessages) {
            if (message.getSender().getId().equals(otherUserId)) {
                message.setIsRead(true);
                messageRepository.save(message);
            }
        }
    }

    public Long getUnreadMessageCount() {
        User currentUser = authService.getCurrentUser();
        return messageRepository.countUnreadMessages(currentUser.getId());
    }

    @Transactional
    public MessageResponse sendSystemMessage(Long userId, String content) {
        User recipient = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Message message = new Message();
        message.setSender(null); // System message
        message.setRecipient(recipient);
        message.setContent(content);
        message.setMessageType(Message.MessageType.SYSTEM);
        message.setIsRead(false);
        
        return modelMapper.map(messageRepository.save(message), MessageResponse.class);
    }
}