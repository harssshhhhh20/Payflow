package com.harsh.payflow.merchant.repository;

import com.harsh.payflow.merchant.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MerchantRepository extends JpaRepository<Merchant, UUID> {

    Optional<Merchant> findByMerchantId(String merchantId);

    Optional<Merchant> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByMerchantId(String merchantId);

    Optional<Merchant> findByApiKeyPrefix(String apiKeyPrefix);
}