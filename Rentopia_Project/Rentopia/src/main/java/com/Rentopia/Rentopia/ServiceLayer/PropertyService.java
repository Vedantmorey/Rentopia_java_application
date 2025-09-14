package com.Rentopia.Rentopia.ServiceLayer;

import com.Rentopia.Rentopia.Entity.Property;
import com.Rentopia.Rentopia.Repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    public List<Property> searchProperties(String keyword, String location, Double maxPrice) {
        if (keyword != null && !keyword.isEmpty() && location != null && !location.isEmpty()) {
            return propertyRepository.findByTitleContainingIgnoreCaseAndLocationContainingIgnoreCase(keyword, location);
        } else if (keyword != null && !keyword.isEmpty()) {
            return propertyRepository.findByTitleContainingIgnoreCase(keyword);
        } else if (location != null && !location.isEmpty()) {
            return propertyRepository.findByLocationContainingIgnoreCase(location);
        } else {
            return propertyRepository.findAll();
        }
    }
}