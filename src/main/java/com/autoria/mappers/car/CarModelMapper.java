package com.autoria.mappers.car;

import com.autoria.models.car.CarBrand;
import com.autoria.models.car.CarModel;
import com.autoria.models.car.dto.CarModelRequestDto;
import com.autoria.models.car.dto.CarModelResponseDto;
import org.springframework.stereotype.Component;

@Component
public class CarModelMapper {

    public CarModel toEntity(CarModelRequestDto carModelRequestDto, CarBrand carBrand) {
        return CarModel.builder()
                .model(carModelRequestDto.getModel())
                .carBrand(carBrand)
                .build();
    }

    public CarModelResponseDto toDto(CarModel carModel) {
        CarModelResponseDto carModelResponseDto = new CarModelResponseDto();
        carModelResponseDto.setId(carModel.getId());
        carModelResponseDto.setModel(carModel.getModel());
        if (carModel.getCarBrand() != null) {
            carModelResponseDto.setBrandId(carModel.getCarBrand().getId());
            carModelResponseDto.setBrandName(carModel.getCarBrand().getName());
        }
        return carModelResponseDto;
    }

}
