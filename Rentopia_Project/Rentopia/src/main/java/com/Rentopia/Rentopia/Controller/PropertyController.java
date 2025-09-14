package com.Rentopia.Rentopia.Controller;

import com.Rentopia.Rentopia.Entity.Property;
import com.Rentopia.Rentopia.Entity.User;
import com.Rentopia.Rentopia.Repository.PropertyRepository;
import com.Rentopia.Rentopia.Repository.UserRepository;
import com.Rentopia.Rentopia.ServiceLayer.PropertyService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Controller
public class PropertyController {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final PropertyService propertyService;
    private final RestTemplate restTemplate;
    private final String locationIQApiKey;

    @Autowired
    public PropertyController(PropertyRepository propertyRepository, UserRepository userRepository, PropertyService propertyService, @Value("${locationiq.api.key}") String locationIQApiKey) {
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.propertyService = propertyService;
        this.restTemplate = new RestTemplate();
        this.locationIQApiKey = locationIQApiKey;
    }

    // ... (showHomePage and showAddPropertyForm methods are unchanged) ...
    @GetMapping("/")
    public String showHomePage(Model model,
                               @RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "location", required = false) String location,
                               @RequestParam(value = "maxPrice", required = false) Double maxPrice) {

        List<Property> properties = propertyService.searchProperties(keyword, location, maxPrice);
        model.addAttribute("properties", properties);
        model.addAttribute("keyword", keyword);
        model.addAttribute("location", location);
        model.addAttribute("maxPrice", maxPrice);

        return "index";
    }

    @GetMapping("/add-property")
    public String showAddPropertyForm(Model model) {
        model.addAttribute("property", new Property());
        return "add-property";
    }


    @PostMapping("/add-property")
    public String saveProperty(@RequestParam("title") String title,
                               @RequestParam("location") String location,
                               @RequestParam("price") Double price,
                               @RequestParam("description") String description,
                               @RequestParam("imageFile") MultipartFile imageFile,
                               Authentication authentication) throws IOException {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // THIS IS THE MISSING LINE
        Property newProperty = new Property();
        newProperty.setTitle(title);
        newProperty.setLocation(location);
        newProperty.setPrice(price);
        newProperty.setDescription(description);
        newProperty.setOwner(user);

        // Geocoding Logic
        try {
            String url = "https://us1.locationiq.com/v1/search.php?key=" + locationIQApiKey + "&q=" + location + "&format=json";
            String response = restTemplate.getForObject(url, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            if (root.isArray() && root.size() > 0) {
                JsonNode firstResult = root.get(0);
                newProperty.setLatitude(firstResult.get("lat").asDouble());
                newProperty.setLongitude(firstResult.get("lon").asDouble());
            }
        } catch (Exception e) {
            System.err.println("Error geocoding address: " + e.getMessage());
        }

        // File Upload Logic
        if (!imageFile.isEmpty()) {
            String originalFileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
            Path uploadDir = Paths.get("uploads");

            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            try (InputStream inputStream = imageFile.getInputStream()) {
                Path filePath = uploadDir.resolve(originalFileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                newProperty.setImageUrl("/uploads/" + originalFileName);
            } catch (IOException e) {
                throw new IOException("Could not save image file: " + originalFileName, e);
            }
        }
        propertyRepository.save(newProperty);
        return "redirect:/";
    }

    // ... (all other methods - showPropertyDetails, deleteProperty, edit methods - remain unchanged) ...
    @GetMapping("/property/{id}")
    public String showPropertyDetails(@PathVariable("id") Long id, Model model) {
        Optional<Property> propertyOpt = propertyRepository.findById(id);
        if (propertyOpt.isPresent()) {
            model.addAttribute("property", propertyOpt.get());
            return "property-details";
        }
        return "redirect:/";
    }

    // ... edit and delete methods ...

}