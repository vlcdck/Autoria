package com.autoria.models.ad;

import com.autoria.enums.AdStatus;
import com.autoria.enums.CurrencyCode;
import com.autoria.enums.FuelType;
import com.autoria.models.car.CarBrand;
import com.autoria.models.car.CarModel;
import com.autoria.models.dealership.Dealership;
import com.autoria.models.user.AppUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "car_ads")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarAd {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private AppUser seller;

    @Enumerated(EnumType.STRING)
    @NotNull
    private AdStatus status;

    @PositiveOrZero
    private Integer editAttempts;

    @ManyToOne
    @JoinColumn(name = "dealership_id")
    private Dealership dealership;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private CarBrand brand;

    @ManyToOne
    @JoinColumn(name = "model_id")
    private CarModel model;

    @Min(1886)
    private int year;

    private Integer mileage;

    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    private Integer ownersCount;

    @Column(length = 100)
    private String region;


    @ElementCollection
    @CollectionTable(name = "car_ad_photos", joinColumns = @JoinColumn(name = "car_ad_id"))
    @Column(name = "photo_url")
    private List<String> photos;

    private String description;

    @Enumerated(EnumType.STRING)
    @NotNull
    private CurrencyCode originalCurrency;

    @Positive
    private BigDecimal price;

    @Positive
    private BigDecimal priceUSD;

    @Positive
    private BigDecimal priceUAH;

    @Positive
    private BigDecimal priceEUR;

    private String exchangeRateSource;

    private LocalDateTime exchangeRateDate;
}

