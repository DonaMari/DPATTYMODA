package com.dpatty.service;

import com.dpatty.dto.order.*;
import com.dpatty.model.*;
import com.dpatty.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductVariantRepository variantRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthService authService;

    @Autowired
    private NotificationService notificationService;

    public Page<OrderResponse> getUserOrders(Pageable pageable) {
        User currentUser = authService.getCurrentUser();
        Page<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId(), pageable);
        return orders.map(order -> modelMapper.map(order, OrderResponse.class));
    }

    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        Page<Order> orders = orderRepository.findAll(pageable);
        return orders.map(order -> modelMapper.map(order, OrderResponse.class));
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        
        User currentUser = authService.getCurrentUser();
        if (!order.getUser().getId().equals(currentUser.getId()) && 
            !hasAdminRole(currentUser)) {
            throw new RuntimeException("No autorizado para ver este pedido");
        }
        
        return modelMapper.map(order, OrderResponse.class);
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        User currentUser = authService.getCurrentUser();
        
        // Get user's cart
        Cart cart = cartRepository.findByUserIdWithItems(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Carrito vacío"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Carrito vacío");
        }

        // Create order
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUser(currentUser);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setOrderType(Order.OrderType.ONLINE);
        
        // Set shipping address
        order.setShippingAddress(request.getShippingAddress());
        order.setShippingCity(request.getShippingCity());
        order.setShippingState(request.getShippingState());
        order.setShippingZipCode(request.getShippingZipCode());
        
        order.setNotes(request.getNotes());

        // Calculate totals
        BigDecimal subtotal = BigDecimal.ZERO;
        
        for (CartItem cartItem : cart.getItems()) {
            ProductVariant variant = cartItem.getProductVariant();
            
            // Check stock
            if (variant.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Stock insuficiente para: " + variant.getProduct().getName());
            }
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductVariant(variant);
            orderItem.setQuantity(cartItem.getQuantity());
            
            BigDecimal unitPrice = variant.getPrice() != null ? variant.getPrice() : variant.getProduct().getPrice();
            orderItem.setUnitPrice(unitPrice);
            orderItem.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            
            // Snapshot product info
            orderItem.setProductName(variant.getProduct().getName());
            orderItem.setProductSku(variant.getSku());
            orderItem.setVariantSize(variant.getSize());
            orderItem.setVariantColor(variant.getColor());
            
            order.getItems().add(orderItem);
            subtotal = subtotal.add(orderItem.getTotalPrice());
        }
        
        order.setSubtotal(subtotal);
        order.setTaxAmount(request.getTaxAmount() != null ? request.getTaxAmount() : BigDecimal.ZERO);
        order.setShippingAmount(request.getShippingAmount() != null ? request.getShippingAmount() : BigDecimal.ZERO);
        order.setDiscountAmount(request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO);
        order.setTotalAmount(subtotal.add(order.getTaxAmount()).add(order.getShippingAmount()).subtract(order.getDiscountAmount()));

        Order savedOrder = orderRepository.save(order);
        
        // Clear cart
        cart.getItems().clear();
        cartRepository.save(cart);
        
        // Send notification
        notificationService.createNotification(
            currentUser.getId(),
            "Pedido Creado",
            "Tu pedido #" + savedOrder.getOrderNumber() + " ha sido creado exitosamente",
            "SUCCESS"
        );
        
        return modelMapper.map(savedOrder, OrderResponse.class);
    }

    @Transactional
    public OrderResponse createPOSOrder(CreatePOSOrderRequest request) {
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setOrderType(Order.OrderType.POS);
        
        // Customer info for POS orders
        order.setCustomerName(request.getCustomerName());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setCustomerPhone(request.getCustomerPhone());
        order.setCustomerDocument(request.getCustomerDocument());
        
        BigDecimal subtotal = BigDecimal.ZERO;
        
        for (CreatePOSOrderRequest.POSOrderItem item : request.getItems()) {
            ProductVariant variant = variantRepository.findById(item.getProductVariantId())
                    .orElseThrow(() -> new RuntimeException("Variante no encontrada"));
            
            if (variant.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("Stock insuficiente para: " + variant.getProduct().getName());
            }
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductVariant(variant);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setUnitPrice(item.getUnitPrice());
            orderItem.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            
            // Snapshot product info
            orderItem.setProductName(variant.getProduct().getName());
            orderItem.setProductSku(variant.getSku());
            orderItem.setVariantSize(variant.getSize());
            orderItem.setVariantColor(variant.getColor());
            
            order.getItems().add(orderItem);
            subtotal = subtotal.add(orderItem.getTotalPrice());
        }
        
        order.setSubtotal(subtotal);
        order.setTaxAmount(request.getTaxAmount() != null ? request.getTaxAmount() : BigDecimal.ZERO);
        order.setDiscountAmount(request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO);
        order.setTotalAmount(subtotal.add(order.getTaxAmount()).subtract(order.getDiscountAmount()));

        return modelMapper.map(orderRepository.save(order), OrderResponse.class);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        
        Order.OrderStatus oldStatus = order.getStatus();
        order.setStatus(status);
        
        // Update stock when order is completed
        if (status == Order.OrderStatus.DELIVERED && oldStatus != Order.OrderStatus.DELIVERED) {
            for (OrderItem item : order.getItems()) {
                ProductVariant variant = item.getProductVariant();
                variant.setStockQuantity(variant.getStockQuantity() - item.getQuantity());
                variantRepository.save(variant);
            }
        }
        
        Order savedOrder = orderRepository.save(order);
        
        // Send notification to customer
        if (order.getUser() != null) {
            String message = getStatusMessage(status, order.getOrderNumber());
            notificationService.createNotification(
                order.getUser().getId(),
                "Estado del Pedido Actualizado",
                message,
                "INFO"
            );
        }
        
        return modelMapper.map(savedOrder, OrderResponse.class);
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        
        User currentUser = authService.getCurrentUser();
        if (!order.getUser().getId().equals(currentUser.getId()) && !hasAdminRole(currentUser)) {
            throw new RuntimeException("No autorizado para cancelar este pedido");
        }
        
        if (order.getStatus() != Order.OrderStatus.PENDING && order.getStatus() != Order.OrderStatus.CONFIRMED) {
            throw new RuntimeException("No se puede cancelar el pedido en su estado actual");
        }
        
        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
        
        // Send notification
        if (order.getUser() != null) {
            notificationService.createNotification(
                order.getUser().getId(),
                "Pedido Cancelado",
                "Tu pedido #" + order.getOrderNumber() + " ha sido cancelado",
                "WARNING"
            );
        }
    }

    private String generateOrderNumber() {
        String prefix = "ORD-";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return prefix + timestamp.substring(timestamp.length() - 6) + "-" + random;
    }

    private String getStatusMessage(Order.OrderStatus status, String orderNumber) {
        return switch (status) {
            case CONFIRMED -> "Tu pedido #" + orderNumber + " ha sido confirmado";
            case PROCESSING -> "Tu pedido #" + orderNumber + " está siendo procesado";
            case SHIPPED -> "Tu pedido #" + orderNumber + " ha sido enviado";
            case DELIVERED -> "Tu pedido #" + orderNumber + " ha sido entregado";
            case CANCELLED -> "Tu pedido #" + orderNumber + " ha sido cancelado";
            case REFUNDED -> "Tu pedido #" + orderNumber + " ha sido reembolsado";
            default -> "El estado de tu pedido #" + orderNumber + " ha sido actualizado";
        };
    }

    private boolean hasAdminRole(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> "ADMIN".equals(role.getName()) || "EMPLOYEE".equals(role.getName()));
    }
}