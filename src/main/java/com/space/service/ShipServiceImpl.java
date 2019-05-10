package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ShipServiceImpl implements ShipService {

    private ShipRepository shipRepository;



    public ShipServiceImpl() {

    }

    @Autowired
    public ShipServiceImpl(ShipRepository shipRepository) {
        super();
        this.shipRepository = shipRepository;
    }

    @Override
    public Ship saveShip(Ship ship) {
        return shipRepository.save(ship);
    }

    @Override
    public Ship getShip(Long id) {
        return shipRepository.findById(id).orElse(null);
    }

    @Override
    public List<Ship> getShips(
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
    ) {
        final Date afterDate = after == null ? null : new Date(after);
        final Date beforeDate = before == null ? null : new Date(before);
        final List<Ship> list = new ArrayList<>();
        shipRepository.findAll().forEach((ship) -> {
            if (name != null && !ship.getName().contains(name)) return;
            if (planet != null && !ship.getPlanet().contains(planet)) return;
            if (shipType != null && ship.getShipType() != shipType) return;
            if (afterDate != null && ship.getProdDate().before(afterDate)) return;
            if (beforeDate != null && ship.getProdDate().after(beforeDate)) return;
            if (isUsed != null && ship.getUsed().booleanValue() != isUsed.booleanValue()) return;
            if (minSpeed != null && ship.getSpeed().compareTo(minSpeed) < 0) return;
            if (maxSpeed != null && ship.getSpeed().compareTo(maxSpeed) > 0) return;
            if (minCrewSize != null && ship.getCrewSize().compareTo(minCrewSize) < 0) return;
            if (maxCrewSize != null && ship.getCrewSize().compareTo(maxCrewSize) > 0) return;
            if (minRating != null && ship.getRating().compareTo(minRating) < 0) return;
            if (maxRating != null && ship.getRating().compareTo(maxRating) > 0) return;

            list.add(ship);
        });
        return list;
    }

    @Override
    public List<Ship> sortShips(List<Ship> ships, ShipOrder order) {
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
        return ships;
    }

    @Override
    public List<Ship> getPage(List<Ship> ships, Integer pageNumber, Integer pageSize) {
        final Integer page = pageNumber == null ? 0 : pageNumber;
        final Integer size = pageSize == null ? 3 : pageSize;
        final int from = page * size;
        int to = from + size;
        if (to > ships.size()) to = ships.size();
        return ships.subList(from, to);
    }

    @Override
    public boolean isValidShip(Ship ship) {
        final Date startProd = getDateForYear(2800);
        final Date endProd = getDateForYear(3019);
        final int maxStringLength = 50;
        final Double minSpeed = 0.01;
        final Double maxSpeed = 0.99;
        final Integer minCrewSize = 1;
        final Integer maxCrewSize = 9999;
        return ship != null && ship.getName() != null
            && !ship.getName().isEmpty() && ship.getName().length() <= maxStringLength
            && ship.getPlanet() != null && !ship.getPlanet().isEmpty() && ship.getPlanet().length() <= maxStringLength
            && ship.getProdDate() != null && ship.getProdDate().after(startProd) && ship.getProdDate().before(endProd)
            && ship.getSpeed() != null && ship.getSpeed().compareTo(minSpeed) >= 0 && ship.getSpeed().compareTo(maxSpeed) <= 0
            && ship.getCrewSize() != null && ship.getCrewSize().compareTo(minCrewSize) >= 0
            && ship.getCrewSize().compareTo(maxCrewSize) <= 0;
    }

    private Date getDateForYear(int year) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }

    private int getYearFromDate(Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    @Override
    public double computeRating(double speed, boolean isUsed, Date prod) {
        final int now = 3019;
        final int prodYear = getYearFromDate(prod);
        final double k = isUsed ? 0.5 : 1;
        return 80 * speed * k / (now - prodYear + 1);
    }
}
