package com.dpatty.service;

import com.dpatty.dto.notification.NotificationResponse;
import com.dpatty.model.Notification;
import com.dpatty.model.User;
import com.dpatty.repository.NotificationRepository;
import com.dpatty.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthService authService;

    public Page<NotificationResponse> getUserNotifications(Pageable pageable) {
        User currentUser = authService.getCurrentUser();
        Page<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId(), pageable);
        return notifications.map(notification -> modelMapper.map(notification, NotificationResponse.class));
    }

    public Page<NotificationResponse> getUnreadNotifications(Pageable pageable) {
        User currentUser = authService.getCurrentUser();
        Page<Notification> notifications = notificationRepository.findUnreadByUserId(currentUser.getId(), pageable);
        return notifications.map(notification -> modelMapper.map(notification, NotificationResponse.class));
    }

    public Long getUnreadNotificationCount() {
        User currentUser = authService.getCurrentUser();
        return notificationRepository.countUnreadNotifications(currentUser.getId());
    }

    @Transactional
    public NotificationResponse createNotification(Long userId, String title, String message, String type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setIsRead(false);
        
        Notification savedNotification = notificationRepository.save(notification);
        return modelMapper.map(savedNotification, NotificationResponse.class);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        
        User currentUser = authService.getCurrentUser();
        if (!notification.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("No autorizado para marcar esta notificación");
        }
        
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead() {
        User currentUser = authService.getCurrentUser();
        Page<Notification> unreadNotifications = notificationRepository.findUnreadByUserId(
            currentUser.getId(), Pageable.unpaged());
        
        for (Notification notification : unreadNotifications) {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        }
    }

    @Transactional
    public void notifyAdmins(String title, String message, String type) {
        List<User> admins = userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> "ADMIN".equals(role.getName()) || "EMPLOYEE".equals(role.getName())))
                .toList();
        
        for (User admin : admins) {
            createNotification(admin.getId(), title, message, type);
        }
    }

    @Transactional
    public void deleteNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        
        User currentUser = authService.getCurrentUser();
        if (!notification.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("No autorizado para eliminar esta notificación");
        }
        
        notificationRepository.delete(notification);
    }
}