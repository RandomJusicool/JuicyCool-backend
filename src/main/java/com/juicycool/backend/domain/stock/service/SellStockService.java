package com.juicycool.backend.domain.stock.service;

import com.juicycool.backend.domain.stock.presentation.dto.request.SellStockRequestDto;

public interface SellStockService {
    void execute(Long stockId, SellStockRequestDto dto);
}
