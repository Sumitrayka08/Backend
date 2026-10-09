package com.agrinexus.service;

import com.agrinexus.dto.CropRecommendationRequest;
import com.agrinexus.dto.CropRecommendationResponse;
import com.agrinexus.entity.CropRecommendation;
import com.agrinexus.entity.User;
import com.agrinexus.repository.CropRecommendationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CropRecommendationService {

    private final CropRecommendationRepository cropRecommendationRepository;

    public CropRecommendationService(CropRecommendationRepository cropRecommendationRepository) {
        this.cropRecommendationRepository = cropRecommendationRepository;
    }

    public CropRecommendationResponse processRecommendation(User user, CropRecommendationRequest req) {
        // Evaluate agronomic rule engine
        AgronomicResult result = evaluateAgronomicRules(req);

        CropRecommendation entity = new CropRecommendation();
        entity.setUser(user);
        entity.setSoilType(req.getSoilType());
        entity.setNitrogen(req.getNitrogen());
        entity.setPhosphorus(req.getPhosphorus());
        entity.setPotassium(req.getPotassium());
        entity.setTemperature(req.getTemperature());
        entity.setHumidity(req.getHumidity());
        entity.setPh(req.getPh());
        entity.setRainfall(req.getRainfall());
        entity.setLocation(req.getLocation());
        entity.setSeason(req.getSeason());

        entity.setRecommendedCrop(result.crop);
        entity.setConfidenceScore(result.confidence);
        entity.setReason(result.reason);
        entity.setGuidance(result.guidance);

        CropRecommendation saved = cropRecommendationRepository.save(entity);

        return new CropRecommendationResponse(
                saved.getId(),
                saved.getRecommendedCrop(),
                saved.getConfidenceScore(),
                saved.getReason(),
                saved.getGuidance(),
                "AGRONOMIC_RULE_ENGINE"
        );
    }

    public List<CropRecommendation> getRecommendations(User user) {
        return cropRecommendationRepository.findByUser(user);
    }

    private AgronomicResult evaluateAgronomicRules(CropRecommendationRequest req) {
        double N = req.getNitrogen();
        double P = req.getPhosphorus();
        double K = req.getPotassium();
        double temp = req.getTemperature();
        double humidity = req.getHumidity();
        double ph = req.getPh();
        double rain = req.getRainfall();

        // 1. Rice: High rainfall (>180mm), high humidity (>70%), N: 60-120, temp: 20-35 C
        if (rain >= 180 && temp >= 20 && temp <= 38 && humidity >= 70 && ph >= 5.0 && ph <= 7.5) {
            return new AgronomicResult(
                    "Rice",
                    94.5,
                    "Optimal warm temperature, high humidity (" + humidity + "%), and abundant rainfall (" + rain + "mm) present ideal conditions for paddy cultivation.",
                    "Maintain 2-5cm standing water during vegetative stage. Apply nitrogenous fertilizer in split doses at tillering and panicle initiation."
            );
        }

        // 2. Wheat: Moderate temperature (12-25 C), rainfall (40-100mm), pH (6.0-7.5), N (80-140)
        if (temp >= 10 && temp <= 26 && rain >= 35 && rain <= 120 && ph >= 5.8 && ph <= 7.8) {
            return new AgronomicResult(
                    "Wheat",
                    92.0,
                    "Cool ambient temperatures (" + temp + "°C) with moderate rainfall (" + rain + "mm) provide prime conditions for wheat germination and grain filling.",
                    "Sow in rows 20-22.5cm apart. Ensure crown root initiation irrigation at 20-25 days after sowing."
            );
        }

        // 3. Maize: Balanced N-P-K, temp (18-32 C), rainfall (60-140mm)
        if (temp >= 18 && temp <= 34 && rain >= 50 && rain <= 150 && N >= 50 && K >= 30) {
            return new AgronomicResult(
                    "Maize (Corn)",
                    89.0,
                    "Well-balanced nitrogen (" + N + " kg/ha) and warm temperatures suitable for rapid maize biomass development.",
                    "Maintain soil moisture during tasseling and silking stages. Earth up soil around plants after top-dressing nitrogen."
            );
        }

        // 4. Cotton: Warm/hot temp (22-38 C), dry-moderate rain (50-110mm), K >= 35
        if (temp >= 21 && temp <= 38 && rain >= 40 && rain <= 130 && K >= 30 && ph >= 6.0 && ph <= 8.2) {
            return new AgronomicResult(
                    "Cotton",
                    88.5,
                    "Hot climates with deep black/alluvial soil and adequate potassium levels produce high lint yield.",
                    "Ensure adequate drainage. Monitor regularly for bollworm and whitefly during flowering."
            );
        }

        // 5. Chickpea / Pulses: Low-moderate moisture, alkaline pH (6.0-8.5), high P/K
        if (temp >= 12 && temp <= 28 && rain <= 90 && P >= 30 && ph >= 6.0) {
            return new AgronomicResult(
                    "Chickpea (Gram)",
                    87.0,
                    "Moderately dry conditions (" + rain + "mm rain) with phosphorus richness suited for root nodulation and pulse growth.",
                    "Avoid waterlogging. Treat seeds with Rhizobium culture prior to sowing to maximize biological nitrogen fixation."
            );
        }

        // 6. Watermelon: Hot temp (>24 C), low rainfall (<100mm), sandy/loam soil, pH 6.0-7.0
        if (temp >= 24 && humidity < 75 && rain <= 120 && ph >= 5.5 && ph <= 7.5) {
            return new AgronomicResult(
                    "Watermelon",
                    86.0,
                    "High ambient temperature (" + temp + "°C) with lower relative humidity ideal for fruit sugar accumulation.",
                    "Use drip irrigation under mulching sheets. Pinch terminal buds to encourage lateral fruiting branches."
            );
        }

        // Default Fallback: General Millet / Sorghum for arid or mixed conditions
        return new AgronomicResult(
                "Sorghum / Pearl Millet",
                82.0,
                "Resilient crop choice matching local soil pH (" + ph + ") and ambient environmental parameters.",
                "Suitable for semi-arid conditions. Requires minimal supplemental irrigation and baseline soil fertility."
        );
    }

    private static class AgronomicResult {
        String crop;
        Double confidence;
        String reason;
        String guidance;

        AgronomicResult(String crop, Double confidence, String reason, String guidance) {
            this.crop = crop;
            this.confidence = confidence;
            this.reason = reason;
            this.guidance = guidance;
        }
    }
}