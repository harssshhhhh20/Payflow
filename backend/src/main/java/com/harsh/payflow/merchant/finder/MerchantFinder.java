package com.harsh.payflow.merchant.finder;

import com.harsh.payflow.merchant.entity.Merchant;
import com.harsh.payflow.merchant.exception.MerchantNotFoundException;
import com.harsh.payflow.merchant.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MerchantFinder {
    private final MerchantRepository merchantRepository;

    public Merchant getByMerchantId(String merchantId) {
        return merchantRepository.findByMerchantId(merchantId)
                .orElseThrow(() -> new MerchantNotFoundException(merchantId));
    }
}
