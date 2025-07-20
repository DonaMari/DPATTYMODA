package com.dpatty.dto.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateMessageRequest {
    @NotNull(message = "ID de destinatario es requerido")
    private Long recipientId;

    @NotBlank(message = "Contenido del mensaje es requerido")
    private String content;
}