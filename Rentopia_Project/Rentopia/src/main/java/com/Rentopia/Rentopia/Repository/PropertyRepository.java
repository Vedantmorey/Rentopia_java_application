package com.Rentopia.Rentopia.Repository;

import com.Rentopia.Rentopia.Entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findByTitleContainingIgnoreCase(String keyword);
    List<Property> findByLocationContainingIgnoreCase(String location);
    List<Property> findByTitleContainingIgnoreCaseAndLocationContainingIgnoreCase(String keyword, String location);
}