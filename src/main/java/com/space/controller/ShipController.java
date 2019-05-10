package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        final List<Ship> sortedShips = shipService.sortShips(ships, order);

        return shipService.getPage(sortedShips, pageNumber, pageSize);
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

    @RequestMapping(path = "/rest/ships", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Ship> createShip(@RequestBody Ship ship) {
        if (!shipService.isValidShip(ship)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (ship.getUsed() == null) ship.setUsed(false);
        ship.setSpeed(round(ship.getSpeed()));
        final double rating = shipService.computeRating(ship.getSpeed(), ship.getUsed(), ship.getProdDate());
        ship.setRating(round(rating));

        final Ship savedShip = shipService.saveShip(ship);

        return new ResponseEntity<>(savedShip, HttpStatus.OK);
    }

    @RequestMapping(path = "/rest/ships/{id}", method = RequestMethod.GET)
    public ResponseEntity<Ship> getShip(@PathVariable(value = "id") String pathId) {
        Long id;
        if (pathId == null) {
            id = null;
        } else try {
            id = Long.parseLong(pathId);
        } catch (NumberFormatException e) {
            id = null;
        }
        if (id == null || id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        final Ship ship = shipService.getShip(id);
        if (ship == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(ship, HttpStatus.OK);
    }

    private double round(double value) {
        return Math.round(value * 100) / 100D;
    }
}
