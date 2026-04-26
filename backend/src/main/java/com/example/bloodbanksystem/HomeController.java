package com.example.bloodbanksystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private DonorService donorService;

    @Autowired
    private DonationService donationService;

    @GetMapping("/")
    public String index(Model model) {
        List<Donor> donors = donorService.getAllDonors();
        model.addAttribute("donorCount", donors.size());
        model.addAttribute("totalDonations", donationService.getAllDonations().size());
        
        // Prepare data for the chart
        Map<String, Long> bloodGroupCounts = donors.stream()
                .collect(Collectors.groupingBy(Donor::getBloodGroup, Collectors.counting()));
        
        model.addAttribute("bloodGroupData", bloodGroupCounts);
        return "index";
    }
}

