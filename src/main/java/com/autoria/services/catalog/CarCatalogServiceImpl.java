package com.autoria.services.catalog;

import com.autoria.models.car.CarBrand;
import com.autoria.models.car.CarModel;
import com.autoria.repository.CarBrandRepository;
import com.autoria.repository.CarModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CarCatalogServiceImpl implements CarCatalogService {
    private final CarBrandRepository carBrandRepository;
    private final CarModelRepository carModelRepository;

    public List<CarBrand> getAllBrands() {
        return carBrandRepository.findAll();
    }

    public List<CarModel> getAllModels() {
        return carModelRepository.findAll();
    }

    public List<CarModel> getModelsByBrand(UUID brandId) {
        return carModelRepository.findByCarBrand_Id(brandId);
    }
}
