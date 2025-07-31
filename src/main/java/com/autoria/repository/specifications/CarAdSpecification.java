package com.autoria.repository.specifications;

import com.autoria.enums.AdStatus;
import com.autoria.models.ad.CarAd;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.UUID;

public class CarAdSpecification {

    public static Specification<CarAd> filterBy(
            AdStatus status,
            UUID brandId,
            UUID modelId,
            Integer yearFrom,
            Integer yearTo,
            Integer mileageFrom,
            Integer mileageTo,
            BigDecimal priceFrom,
            BigDecimal priceTo,
            String descriptionContains
    ) {
        return (Root<CarAd> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = cb.conjunction();

            if (status != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), status));
            }
            if (brandId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("brand").get("id"), brandId));
            }
            if (modelId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("model").get("id"), modelId));
            }
            if (yearFrom != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("year"), yearFrom));
            }
            if (yearTo != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("year"), yearTo));
            }
            if (mileageFrom != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("mileage"), mileageFrom));
            }
            if (mileageTo != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("mileage"), mileageTo));
            }
            if (priceFrom != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("price"), priceFrom));
            }
            if (priceTo != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("price"), priceTo));
            }
            if (descriptionContains != null && !descriptionContains.isEmpty()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("description")), "%" + descriptionContains.toLowerCase() + "%"));
            }
            return predicate;
        };
    }
    public static Specification<CarAd> ownedByUser(UUID userId) {
        return (
                root,
                query,
                cb) -> cb.equal(root.get("seller").get("id"), userId);
    }
}