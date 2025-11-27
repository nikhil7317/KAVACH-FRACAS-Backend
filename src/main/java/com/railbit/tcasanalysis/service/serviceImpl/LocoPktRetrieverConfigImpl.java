package com.railbit.tcasanalysis.service.serviceImpl;

import com.railbit.tcasanalysis.entity.LocoPktRetrieverConfig;
import com.railbit.tcasanalysis.repository.LocoPktRetrieverConfigRepo;
import com.railbit.tcasanalysis.service.LocoPktRetrieverConfigService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LocoPktRetrieverConfigImpl implements LocoPktRetrieverConfigService {
    private final LocoPktRetrieverConfigRepo locoPktRetrieverConfigRepo;


    @Override
    public LocoPktRetrieverConfig getLocoPktRetrieverConfig() {
        return locoPktRetrieverConfigRepo.findAnySingleRecord();
    }

    @Override
    public void updateLocoPktRetrieverConfig(LocoPktRetrieverConfig config) {
        locoPktRetrieverConfigRepo.save(config);
    }
}
