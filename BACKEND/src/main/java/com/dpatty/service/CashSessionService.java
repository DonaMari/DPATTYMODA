package com.dpatty.service;

import com.dpatty.dto.pos.CashSessionResponse;
import com.dpatty.dto.pos.OpenCashSessionRequest;
import com.dpatty.dto.pos.CloseCashSessionRequest;
import com.dpatty.model.CashSession;
import com.dpatty.model.Store;
import com.dpatty.model.User;
import com.dpatty.repository.CashSessionRepository;
import com.dpatty.repository.StoreRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class CashSessionService {

    @Autowired
    private CashSessionRepository cashSessionRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthService authService;

    public Optional<CashSessionResponse> getActiveCashSession() {
        User currentUser = authService.getCurrentUser();
        Optional<CashSession> activeSession = cashSessionRepository.findByCashierIdAndIsActiveTrue(currentUser.getId());
        return activeSession.map(session -> modelMapper.map(session, CashSessionResponse.class));
    }

    public Page<CashSessionResponse> getCashSessionHistory(Pageable pageable) {
        Page<CashSession> sessions = cashSessionRepository.findAll(pageable);
        return sessions.map(session -> modelMapper.map(session, CashSessionResponse.class));
    }

    @Transactional
    public CashSessionResponse openCashSession(OpenCashSessionRequest request) {
        User currentUser = authService.getCurrentUser();
        
        // Check if user already has an active session
        Optional<CashSession> existingSession = cashSessionRepository.findByCashierIdAndIsActiveTrue(currentUser.getId());
        if (existingSession.isPresent()) {
            throw new RuntimeException("Ya tienes una sesión de caja activa");
        }
        
        Store store = null;
        if (request.getStoreId() != null) {
            store = storeRepository.findById(request.getStoreId())
                    .orElseThrow(() -> new RuntimeException("Tienda no encontrada"));
        }
        
        CashSession session = new CashSession();
        session.setCashier(currentUser);
        session.setStore(store);
        session.setSessionNumber(generateSessionNumber());
        session.setOpeningAmount(request.getOpeningAmount());
        session.setOpenedAt(LocalDateTime.now());
        session.setIsActive(true);
        session.setNotes(request.getNotes());
        
        CashSession savedSession = cashSessionRepository.save(session);
        return modelMapper.map(savedSession, CashSessionResponse.class);
    }

    @Transactional
    public CashSessionResponse closeCashSession(CloseCashSessionRequest request) {
        User currentUser = authService.getCurrentUser();
        
        CashSession session = cashSessionRepository.findByCashierIdAndIsActiveTrue(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("No tienes una sesión de caja activa"));
        
        session.setClosingAmount(request.getClosingAmount());
        session.setExpectedAmount(request.getExpectedAmount());
        session.setCashSalesAmount(request.getCashSalesAmount());
        session.setCardSalesAmount(request.getCardSalesAmount());
        session.setDigitalSalesAmount(request.getDigitalSalesAmount());
        session.setTotalSalesAmount(
            request.getCashSalesAmount()
                .add(request.getCardSalesAmount())
                .add(request.getDigitalSalesAmount())
        );
        session.setExpensesAmount(request.getExpensesAmount());
        session.setClosedAt(LocalDateTime.now());
        session.setIsActive(false);
        session.setNotes(session.getNotes() + "\n" + request.getClosingNotes());
        
        CashSession savedSession = cashSessionRepository.save(session);
        return modelMapper.map(savedSession, CashSessionResponse.class);
    }

    public CashSessionResponse getCashSessionById(Long sessionId) {
        CashSession session = cashSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Sesión de caja no encontrada"));
        return modelMapper.map(session, CashSessionResponse.class);
    }

    private String generateSessionNumber() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
        return "CASH-" + date + "-" + time;
    }
}