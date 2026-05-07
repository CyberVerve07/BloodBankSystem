package com.example.bloodbanksystem;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BloodRequestService {

    private final BloodRequestRepository requestRepository;
    private final InventoryService inventoryService;

    public BloodRequestService(BloodRequestRepository requestRepository, InventoryService inventoryService) {
        this.requestRepository = requestRepository;
        this.inventoryService = inventoryService;
    }

    public List<BloodRequest> getAllRequests() {
        return requestRepository.findAllByOrderByRequestDateDesc();
    }

    public Optional<BloodRequest> getRequestById(Long id) {
        return requestRepository.findById(id);
    }

    public List<BloodRequest> getRequestsByStatus(BloodRequest.RequestStatus status) {
        return requestRepository.findByStatus(status);
    }

    public BloodRequest createRequest(BloodRequest request) {
        request.setRequestDate(LocalDateTime.now());
        request.setStatus(BloodRequest.RequestStatus.PENDING);
        return requestRepository.save(request);
    }

    public BloodRequest approveRequest(Long id) {
        BloodRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        request.setStatus(BloodRequest.RequestStatus.APPROVED);
        return requestRepository.save(request);
    }

    public BloodRequest fulfillRequest(Long id) {
        BloodRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        boolean success = inventoryService.useUnits(request.getBloodGroup(), request.getUnitsNeeded());
        if (!success) {
            throw new IllegalArgumentException("Not enough units available for " + request.getBloodGroup());
        }

        request.setStatus(BloodRequest.RequestStatus.FULFILLED);
        request.setResolvedDate(LocalDateTime.now());
        return requestRepository.save(request);
    }

    public BloodRequest rejectRequest(Long id, String reason) {
        BloodRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        request.setStatus(BloodRequest.RequestStatus.REJECTED);
        request.setNotes(reason);
        request.setResolvedDate(LocalDateTime.now());
        return requestRepository.save(request);
    }

    public long getPendingCount() {
        return requestRepository.countByStatus(BloodRequest.RequestStatus.PENDING);
    }
}
