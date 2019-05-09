package com.space.service;

import com.space.model.Ship;
import com.space.model.ShipType;

import java.util.List;

public interface ShipService {

    List<Ship> getShips(
        String name,
        String planet,
        ShipType shipType,
        Long after,
        Long before,
        Boolean isUsed,
        Double minSpeed,
        Double maxSpeed,
        Integer minCrewSize,
        Integer maxCrewSize,
        Double minRating,
        Double maxRating
    );
}
