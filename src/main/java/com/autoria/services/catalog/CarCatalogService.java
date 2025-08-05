package com.autoria.services.catalog;

import com.autoria.models.car.CarBrand;
import com.autoria.models.car.CarModel;

import java.util.List;
import java.util.UUID;

public interface CarCatalogService {
    List<CarBrand> getAllBrands();
    List<CarModel> getAllModels();
    List<CarModel> getModelsByBrand(UUID brandId);
}
