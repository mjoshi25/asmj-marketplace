package com.asmj.marketplace.rental.route;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.*;
public interface RentalRouteRepository extends MongoRepository<RentalRoute,String>{
 Optional<RentalRoute> findByFromLocationIdAndToLocationId(String from,String to);
 List<RentalRoute> findByActiveTrueOrderByFromStateAscFromCityAscToCityAsc();
}
