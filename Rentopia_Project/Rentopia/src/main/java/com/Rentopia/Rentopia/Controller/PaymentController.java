package com.Rentopia.Rentopia.Controller;

import com.Rentopia.Rentopia.Entity.Property;
import com.Rentopia.Rentopia.Repository.PropertyRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PaymentController {

    private final PropertyRepository propertyRepository;

    public PaymentController(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    /**
     * Step 1: User clicks "Pay" and is sent to a processing page.
     */
    @PostMapping("/pay")
    public String startPaymentProcess(@RequestParam Long propertyId, Authentication authentication, Model model) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid property Id:" + propertyId));

        // Add the property and user info to the model for the processing page
        model.addAttribute("property", property);
        model.addAttribute("userEmail", authentication.getName());

        return "payment-processing";
    }

    /**
     * Step 2: The processing page automatically calls this endpoint to get a result.
     */
    @GetMapping("/simulate-payment")
    public String simulatePaymentResult() {
        // Randomly decide if the payment was successful or failed
        boolean paymentSuccess = new java.util.Random().nextBoolean();

        if (paymentSuccess) {
            String mockPaymentId = "PAY-" + System.currentTimeMillis();
            return "redirect:/payment-callback?payment_id=" + mockPaymentId + "&payment_status=Credit";
        } else {
            return "redirect:/payment-callback?payment_status=Failed";
        }
    }

    /**
     * Step 3: The user is redirected here to see the final status.
     */
    @GetMapping("/payment-callback")
    public String showPaymentStatus(@RequestParam(value = "payment_id", required = false) String paymentId,
                                    @RequestParam(value = "payment_status", required = false) String status,
                                    Model model) {
        model.addAttribute("status", status);
        model.addAttribute("paymentId", paymentId);
        return "payment-status";
    }
}