package com.example.bloodbanksystem;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.List;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DonorServiceTest {

    @Mock
    private DonorRepository donorRepository;

    @InjectMocks
    private DonorService donorService;

    private Donor donor;

    @BeforeEach
    void setUp() {
        donor = new Donor("John Doe", "Richard Doe", "john@example.com", "A+", "1234567890");
        donor.setId(1L);
    }

    @Test
    void testGetAllDonors() {
        when(donorRepository.findAll()).thenReturn(Arrays.asList(donor));
        List<Donor> donors = donorService.getAllDonors();
        assertEquals(1, donors.size());
        assertEquals("John Doe", donors.get(0).getName());
    }

    @Test
    void testSaveDonor_Success() {
        when(donorRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(donorRepository.findByContact(anyString())).thenReturn(Optional.empty());
        when(donorRepository.save(any(Donor.class))).thenReturn(donor);

        Donor savedDonor = donorService.saveDonor(donor);
        assertNotNull(savedDonor);
        verify(donorRepository, times(1)).save(any(Donor.class));
    }

    @Test
    void testSaveDonor_DuplicateEmail() {
        when(donorRepository.findByEmail(donor.getEmail())).thenReturn(Optional.of(donor));
        
        assertThrows(IllegalArgumentException.class, () -> {
            donorService.saveDonor(donor);
        });
    }

    @Test
    void testGetDonorById() {
        when(donorRepository.findById(1L)).thenReturn(Optional.of(donor));
        Optional<Donor> found = donorService.getDonorById(1L);
        assertTrue(found.isPresent());
        assertEquals("John Doe", found.get().getName());
    }
}
