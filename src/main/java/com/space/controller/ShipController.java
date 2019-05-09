package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ShipController {

    private ShipService shipService;

    public ShipController() {
    }

    @Autowired
    public ShipController(ShipService shipService) {
        this.shipService = shipService;
    }

    @RequestMapping(path = "/rest/ships", method = RequestMethod.GET)
    public List<Ship> getAllShips(
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "planet", required = false) String planet,
        @RequestParam(value = "shipType", required = false) ShipType shipType,
        @RequestParam(value = "after", required = false) Long after,
        @RequestParam(value = "before", required = false) Long before,
        @RequestParam(value = "isUsed", required = false) Boolean isUsed,
        @RequestParam(value = "minSpeed", required = false) Double minSpeed,
        @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
        @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
        @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
        @RequestParam(value = "minRating", required = false) Double minRating,
        @RequestParam(value = "maxRating", required = false) Double maxRating,
        @RequestParam(value = "order", required = false) ShipOrder order,
        @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
        @RequestParam(value = "pageSize", required = false) Integer pageSize
    ) {
        final List<Ship> ships = shipService.getShips(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed,
            minCrewSize, maxCrewSize, minRating, maxRating);

        if (order != null) {
            ships.sort((ship1, ship2) -> {
                switch (order) {
                    case ID: return ship1.getId().compareTo(ship2.getId());
                    case SPEED: return ship1.getSpeed().compareTo(ship2.getSpeed());
                    case DATE: return ship1.getProdDate().compareTo(ship2.getProdDate());
                    case RATING: return ship1.getRating().compareTo(ship2.getRating());
                    default: return 0;
                }
            });
        }

        final Integer page = pageNumber == null ? 0 : pageNumber;
        final Integer size = pageSize == null ? 3 : pageSize;
        final int from = page * size;
        int to = from + size;
        if (to > ships.size()) to = ships.size();
        return ships.subList(from, to);
    }

    @RequestMapping(path = "/rest/ships/count", method = RequestMethod.GET)
    public Integer getShipsCount(
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "planet", required = false) String planet,
        @RequestParam(value = "shipType", required = false) ShipType shipType,
        @RequestParam(value = "after", required = false) Long after,
        @RequestParam(value = "before", required = false) Long before,
        @RequestParam(value = "isUsed", required = false) Boolean isUsed,
        @RequestParam(value = "minSpeed", required = false) Double minSpeed,
        @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
        @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
        @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
        @RequestParam(value = "minRating", required = false) Double minRating,
        @RequestParam(value = "maxRating", required = false) Double maxRating
    ) {
        return shipService.getShips(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed,
            minCrewSize, maxCrewSize, minRating, maxRating).size();
    }
}
