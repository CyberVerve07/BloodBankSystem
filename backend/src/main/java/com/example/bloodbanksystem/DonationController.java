package com.example.bloodbanksystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/donations")
public class DonationController {

    private final DonationService donationService;
    private final DonorService donorService;

    public DonationController(DonationService donationService, DonorService donorService) {
        this.donationService = donationService;
        this.donorService = donorService;
    }

    @GetMapping
    public String listDonations(@RequestParam(value = "bloodGroup", required = false) String bloodGroup, Model model) {
        List<Donation> donations;
        if (bloodGroup != null && !bloodGroup.isEmpty()) {
            donations = donationService.searchByBloodGroup(bloodGroup);
        } else {
            donations = donationService.getAllDonations();
        }
        model.addAttribute("donations", donations);
        model.addAttribute("donation", new Donation());
        return "donations";
    }

    @GetMapping("/donor/{donorId}")
    public String listDonationsByDonor(@PathVariable Long donorId, Model model) {
        Donor donor = donorService.getDonorById(donorId).orElseThrow(() -> new IllegalArgumentException("Invalid donor Id:" + donorId));
        List<Donation> donations = donationService.getDonationsByDonorId(donorId);
        model.addAttribute("donor", donor);
        model.addAttribute("donations", donations);
        model.addAttribute("donation", new Donation());
        return "donor-donations";
    }

    @GetMapping("/add/{donorId}")
    public String showAddForm(@PathVariable Long donorId, Model model) {
        Donor donor = donorService.getDonorById(donorId).orElseThrow(() -> new IllegalArgumentException("Invalid donor Id:" + donorId));
        Donation donation = new Donation();
        donation.setDonor(donor);
        donation.setBloodGroup(donor.getBloodGroup());
        donation.setDonationDate(LocalDate.now());
        model.addAttribute("donation", donation);
        return "add-donation";
    }

    @PostMapping("/add")
    public String addDonation(@Valid @ModelAttribute("donation") Donation donation, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("donor", donation.getDonor());
            return "add-donation";
        }
        try {
            donationService.saveDonation(donation);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("donor", donation.getDonor());
            return "add-donation";
        }
        return "redirect:/donations/donor/" + donation.getDonor().getId();
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Donation donation = donationService.getDonationById(id).orElseThrow(() -> new IllegalArgumentException("Invalid donation Id:" + id));
        model.addAttribute("donation", donation);
        return "edit-donation";
    }

    @PostMapping("/edit/{id}")
    public String updateDonation(@PathVariable Long id, @Valid @ModelAttribute("donation") Donation donation, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "edit-donation";
        }
        try {
            donationService.updateDonation(id, donation);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "edit-donation";
        }
        return "redirect:/donations/donor/" + donation.getDonor().getId();
    }

    @GetMapping("/delete/{id}")
    public String deleteDonation(@PathVariable Long id) {
        Donation donation = donationService.getDonationById(id).orElseThrow(() -> new IllegalArgumentException("Invalid donation Id:" + id));
        Long donorId = donation.getDonor().getId();
        donationService.deleteDonation(id);
        return "redirect:/donations/donor/" + donorId;
    }
}