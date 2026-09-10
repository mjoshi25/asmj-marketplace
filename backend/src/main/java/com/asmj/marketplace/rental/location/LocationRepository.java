package com.asmj.marketplace.rental.location;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.*;
public interface LocationRepository extends MongoRepository<LocationMaster,String>{
 List<LocationMaster> findByActiveTrueOrderByStateNameAscCityNameAsc();
 Optional<LocationMaster> findByStateCodeIgnoreCaseAndCityNameIgnoreCase(String stateCode,String cityName);
}
