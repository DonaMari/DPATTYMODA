package com.dpatty.controller;

import com.dpatty.dto.pos.*;
import com.dpatty.service.CashSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/pos")
@PreAuthorize("hasRole('ADMIN') or hasRole('CASHIER') or hasRole('EMPLOYEE')")
@Tag(name = "POS", description = "Sistema de Punto de Venta")
public class POSController {

    @Autowired
    private CashSessionService cashSessionService;

    @GetMapping("/cash-session/active")
    @Operation(summary = "Obtener sesión de caja activa")
    public ResponseEntity<?> getActiveCashSession() {
        Optional<CashSessionResponse> activeSession = cashSessionService.getActiveCashSession();
        if (activeSession.isPresent()) {
            return ResponseEntity.ok(activeSession.get());
        } else {
            return ResponseEntity.ok().body(new Object() {
                public final String message = "No hay sesión de caja activa";
                public final boolean hasActiveSession = false;
            });
        }
    }

    @GetMapping("/cash-sessions")
    @Operation(summary = "Obtener historial de sesiones de caja")
    public ResponseEntity<Page<CashSessionResponse>> getCashSessionHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CashSessionResponse> sessions = cashSessionService.getCashSessionHistory(pageable);
        return ResponseEntity.ok(sessions);
    }

    @PostMapping("/cash-session/open")
    @Operation(summary = "Abrir sesión de caja")
    public ResponseEntity<CashSessionResponse> openCashSession(@Valid @RequestBody OpenCashSessionRequest request) {
        CashSessionResponse session = cashSessionService.openCashSession(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(session);
    }

    @PostMapping("/cash-session/close")
    @Operation(summary = "Cerrar sesión de caja")
    public ResponseEntity<CashSessionResponse> closeCashSession(@Valid @RequestBody CloseCashSessionRequest request) {
        CashSessionResponse session = cashSessionService.closeCashSession(request);
        return ResponseEntity.ok(session);
    }

    @GetMapping("/cash-session/{sessionId}")
    @Operation(summary = "Obtener sesión de caja por ID")
    public ResponseEntity<CashSessionResponse> getCashSessionById(@PathVariable Long sessionId) {
        CashSessionResponse session = cashSessionService.getCashSessionById(sessionId);
        return ResponseEntity.ok(session);
    }
}