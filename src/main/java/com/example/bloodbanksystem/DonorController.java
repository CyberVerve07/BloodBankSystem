package com.example.bloodbanksystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/donors")
public class DonorController {

    @Autowired
    private DonorService donorService;

    @GetMapping
    public String listDonors(@RequestParam(value = "bloodGroup", required = false) String bloodGroup, Model model) {
        List<Donor> donors;
        if (bloodGroup != null && !bloodGroup.isEmpty()) {
            donors = donorService.searchByBloodGroup(bloodGroup.toUpperCase());
        } else {
            donors = donorService.getAllDonors();
        }
        model.addAttribute("donors", donors);
        model.addAttribute("donor", new Donor()); // for form
        return "donors";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("donor", new Donor());
        return "add-donor";
    }

    @PostMapping("/add")
    public String addDonor(@Valid @ModelAttribute("donor") Donor donor, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add-donor";
        }
        try {
            donorService.saveDonor(donor);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "add-donor";
        }
        return "redirect:/donors";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Donor donor = donorService.getDonorById(id).orElseThrow(() -> new IllegalArgumentException("Invalid donor Id:" + id));
        model.addAttribute("donor", donor);
        return "edit-donor";
    }

    @PostMapping("/edit/{id}")
    public String updateDonor(@PathVariable Long id, @Valid @ModelAttribute("donor") Donor donor, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "edit-donor";
        }
        try {
            donorService.updateDonor(id, donor);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "edit-donor";
        }
        return "redirect:/donors";
    }

    @GetMapping("/delete/{id}")
    public String deleteDonor(@PathVariable Long id) {
        donorService.deleteDonor(id);
        return "redirect:/donors";
    }

    @GetMapping("/search")
    public String search(@RequestParam String bloodGroup, Model model) {
        List<Donor> donors = donorService.searchByBloodGroup(bloodGroup.toUpperCase());
        model.addAttribute("donors", donors);
        model.addAttribute("donor", new Donor());
        return "donors";
    }
}