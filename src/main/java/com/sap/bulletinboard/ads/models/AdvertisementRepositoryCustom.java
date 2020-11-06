package com.sap.bulletinboard.ads.models;
import java.util.List;

public interface AdvertisementRepositoryCustom {
    
    /**
     * Returns the entity found by given Id
     * 
     * @param sort
     * @return all entities sorted by the given options
     */
    List<Advertisement> findByTitle(String title);

}
