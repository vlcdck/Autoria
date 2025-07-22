package com.autoria.mappers;

import com.autoria.models.dealership.Dealership;
import com.autoria.models.dealership.dto.DealershipRequestDto;
import com.autoria.models.dealership.dto.DealershipResponseDto;
import org.springframework.stereotype.Component;

@Component
public class DealershipMapper {

    public Dealership toEntity(DealershipRequestDto dealershipRequestDto) {
        return Dealership.builder()
                .name(dealershipRequestDto.getName())
                .address(dealershipRequestDto.getAddress())
                .phone(dealershipRequestDto.getPhone())
                .phone(dealershipRequestDto.getPhone())
                .build();
    }

    public DealershipResponseDto toDto(Dealership dealership) {
        DealershipResponseDto dealershipResponseDto = new DealershipResponseDto();

        dealershipResponseDto.setId(dealership.getId());
        dealershipResponseDto.setName(dealership.getName());
        dealershipResponseDto.setAddress(dealership.getAddress());
        dealershipResponseDto.setPhone(dealership.getPhone());
        dealershipResponseDto.setCreatedAt(dealership.getCreatedAt());
        dealershipResponseDto.setUpdatedAt(dealership.getUpdatedAt());
        return dealershipResponseDto;
    }
}
