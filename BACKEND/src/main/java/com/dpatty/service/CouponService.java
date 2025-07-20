package com.dpatty.service;

import com.dpatty.dto.coupon.CouponResponse;
import com.dpatty.dto.coupon.CreateCouponRequest;
import com.dpatty.dto.coupon.ValidateCouponRequest;
import com.dpatty.dto.coupon.ValidateCouponResponse;
import com.dpatty.model.Coupon;
import com.dpatty.repository.CouponRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Page<CouponResponse> getAllCoupons(Pageable pageable) {
        Page<Coupon> coupons = couponRepository.findAll(pageable);
        return coupons.map(coupon -> modelMapper.map(coupon, CouponResponse.class));
    }

    public List<CouponResponse> getActiveCoupons() {
        List<Coupon> coupons = couponRepository.findActiveCoupons(LocalDateTime.now());
        return coupons.stream()
                .map(coupon -> modelMapper.map(coupon, CouponResponse.class))
                .toList();
    }

    @Transactional
    public CouponResponse createCoupon(CreateCouponRequest request) {
        if (couponRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("El código de cupón ya existe");
        }
        
        Coupon coupon = modelMapper.map(request, Coupon.class);
        coupon.setUsedCount(0);
        coupon.setIsActive(true);
        
        Coupon savedCoupon = couponRepository.save(coupon);
        return modelMapper.map(savedCoupon, CouponResponse.class);
    }

    @Transactional
    public CouponResponse updateCoupon(Long couponId, CreateCouponRequest request) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Cupón no encontrado"));
        
        // Check if code is being changed and if new code already exists
        if (!coupon.getCode().equals(request.getCode()) && couponRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("El código de cupón ya existe");
        }
        
        coupon.setCode(request.getCode());
        coupon.setName(request.getName());
        coupon.setDescription(request.getDescription());
        coupon.setDiscountType(request.getDiscountType());
        coupon.setDiscountValue(request.getDiscountValue());
        coupon.setMinimumAmount(request.getMinimumAmount());
        coupon.setMaximumDiscount(request.getMaximumDiscount());
        coupon.setUsageLimit(request.getUsageLimit());
        coupon.setValidFrom(request.getValidFrom());
        coupon.setValidUntil(request.getValidUntil());
        
        Coupon savedCoupon = couponRepository.save(coupon);
        return modelMapper.map(savedCoupon, CouponResponse.class);
    }

    public ValidateCouponResponse validateCoupon(ValidateCouponRequest request) {
        Coupon coupon = couponRepository.findByCodeAndIsActiveTrue(request.getCouponCode())
                .orElse(null);
        
        ValidateCouponResponse response = new ValidateCouponResponse();
        response.setCouponCode(request.getCouponCode());
        response.setOrderAmount(request.getOrderAmount());
        
        if (coupon == null) {
            response.setValid(false);
            response.setMessage("Cupón no válido o inactivo");
            return response;
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        // Check validity dates
        if (now.isBefore(coupon.getValidFrom()) || now.isAfter(coupon.getValidUntil())) {
            response.setValid(false);
            response.setMessage("Cupón expirado o aún no válido");
            return response;
        }
        
        // Check usage limit
        if (coupon.getUsageLimit() != null && coupon.getUsedCount() >= coupon.getUsageLimit()) {
            response.setValid(false);
            response.setMessage("Cupón agotado");
            return response;
        }
        
        // Check minimum amount
        if (coupon.getMinimumAmount() != null && request.getOrderAmount().compareTo(coupon.getMinimumAmount()) < 0) {
            response.setValid(false);
            response.setMessage("Monto mínimo requerido: S/ " + coupon.getMinimumAmount());
            return response;
        }
        
        // Calculate discount
        BigDecimal discountAmount;
        if (coupon.getDiscountType() == Coupon.DiscountType.PERCENTAGE) {
            discountAmount = request.getOrderAmount()
                    .multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100));
            
            // Apply maximum discount limit
            if (coupon.getMaximumDiscount() != null && discountAmount.compareTo(coupon.getMaximumDiscount()) > 0) {
                discountAmount = coupon.getMaximumDiscount();
            }
        } else {
            discountAmount = coupon.getDiscountValue();
        }
        
        // Ensure discount doesn't exceed order amount
        if (discountAmount.compareTo(request.getOrderAmount()) > 0) {
            discountAmount = request.getOrderAmount();
        }
        
        response.setValid(true);
        response.setDiscountAmount(discountAmount);
        response.setFinalAmount(request.getOrderAmount().subtract(discountAmount));
        response.setCouponName(coupon.getName());
        response.setMessage("Cupón aplicado exitosamente");
        
        return response;
    }

    @Transactional
    public void useCoupon(String couponCode) {
        Coupon coupon = couponRepository.findByCodeAndIsActiveTrue(couponCode)
                .orElseThrow(() -> new RuntimeException("Cupón no encontrado"));
        
        coupon.setUsedCount(coupon.getUsedCount() + 1);
        couponRepository.save(coupon);
    }

    @Transactional
    public void deactivateCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Cupón no encontrado"));
        
        coupon.setIsActive(false);
        couponRepository.save(coupon);
    }

    @Transactional
    public void activateCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Cupón no encontrado"));
        
        coupon.setIsActive(true);
        couponRepository.save(coupon);
    }
}