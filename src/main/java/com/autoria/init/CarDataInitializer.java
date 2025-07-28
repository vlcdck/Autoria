package com.autoria.init;

import com.autoria.models.car.CarBrand;
import com.autoria.models.car.CarModel;
import com.autoria.models.car.dto.CarBrandModelsDto;
import com.autoria.repository.CarBrandRepository;
import com.autoria.repository.CarModelRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CarDataInitializer {

    private final CarBrandRepository carBrandRepository;
    private final CarModelRepository carModelRepository;

    @PostConstruct
    public void initCarBrandsAndModels() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = new ClassPathResource("car-models.json").getInputStream();

            List<CarBrandModelsDto> data = mapper.readValue(inputStream, new TypeReference<>() {
            });
            int brandsAdded = 0;
            int modelsAdded = 0;

            for (CarBrandModelsDto entry : data) {
                CarBrand brand = carBrandRepository.findByNameIgnoreCase(entry.getBrand()).orElse(null);

                if (brand == null) {
                    brand = carBrandRepository.save(CarBrand.builder().name(entry.getBrand()).build());
                    brandsAdded++;
                }

                for (String modelName : entry.getModels()) {
                    boolean exists = carModelRepository.existsByCarBrandAndModelIgnoreCase(brand, modelName);
                    if (!exists) {
                        carModelRepository.save(CarModel.builder()
                                .carBrand(brand)
                                .model(modelName)
                                .build());
                        modelsAdded++;
                    }
                }
            }

            log.info("✅Car brand/model init done. Brands added: {}, Models added: {}", brandsAdded, modelsAdded);

        } catch (Exception e) {
            log.error("Error initializing car brands/models", e);
        }
    }
}