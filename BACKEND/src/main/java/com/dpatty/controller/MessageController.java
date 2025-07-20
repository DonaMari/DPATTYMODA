package com.dpatty.controller;

import com.dpatty.dto.message.CreateMessageRequest;
import com.dpatty.dto.message.MessageResponse;
import com.dpatty.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
@Tag(name = "Messages", description = "Sistema de mensajería interna")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping
    @Operation(summary = "Obtener mensajes del usuario")
    public ResponseEntity<Page<MessageResponse>> getUserMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<MessageResponse> messages = messageService.getUserMessages(pageable);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/conversation/{otherUserId}")
    @Operation(summary = "Obtener conversación con otro usuario")
    public ResponseEntity<List<MessageResponse>> getConversation(@PathVariable Long otherUserId) {
        List<MessageResponse> messages = messageService.getConversation(otherUserId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping
    @Operation(summary = "Enviar mensaje")
    public ResponseEntity<MessageResponse> sendMessage(@Valid @RequestBody CreateMessageRequest request) {
        MessageResponse message = messageService.sendMessage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Marcar mensaje como leído")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        messageService.markAsRead(id);
        return ResponseEntity.ok().body("Mensaje marcado como leído");
    }

    @PutMapping("/conversation/{otherUserId}/read")
    @Operation(summary = "Marcar conversación como leída")
    public ResponseEntity<?> markConversationAsRead(@PathVariable Long otherUserId) {
        messageService.markConversationAsRead(otherUserId);
        return ResponseEntity.ok().body("Conversación marcada como leída");
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Obtener cantidad de mensajes no leídos")
    public ResponseEntity<?> getUnreadMessageCount() {
        Long count = messageService.getUnreadMessageCount();
        return ResponseEntity.ok().body(new Object() {
            public final Long unreadCount = count;
        });
    }
}