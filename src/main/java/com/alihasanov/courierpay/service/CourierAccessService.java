package com.alihasanov.courierpay.service;

import com.alihasanov.courierpay.entity.AppUser;
import com.alihasanov.courierpay.enums.RoleName;
import com.alihasanov.courierpay.exception.BusinessException;
import com.alihasanov.courierpay.exception.NotFoundException;
import com.alihasanov.courierpay.repository.CourierRepository;
import com.alihasanov.courierpay.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static com.alihasanov.courierpay.exception.CourierPayErrorResponse.*;

@Service
public class CourierAccessService {
    private final UserRepository userRepository;
    private final CourierRepository courierRepository;

    public CourierAccessService(UserRepository userRepository, CourierRepository courierRepository) {
        this.userRepository = userRepository;
        this.courierRepository = courierRepository;
    }

    public void assertCanAccessCourier(Long courierId) {
        if (!isCurrentUserCourier()) {
            return;
        }

        var currentCourierId = getCurrentCourierId();
        if (!currentCourierId.equals(courierId)) {
            throw new BusinessException(COURIER_ACCESS_DENIED);
        }
    }

    public boolean isCurrentUserCourier() {
        return getCurrentUser().getRole() == RoleName.COURIER;
    }

    public Long getCurrentCourierId() {
        return courierRepository.findByUserEmail(getCurrentUserEmail())
                .orElseThrow(() -> new NotFoundException(COURIER_NOT_FOUND))
                .getId();
    }

    private AppUser getCurrentUser() {
        return userRepository.findByEmail(getCurrentUserEmail())
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new BusinessException(AUTHENTICATION_REQUIRED);
        }
        return authentication.getName();
    }
}
