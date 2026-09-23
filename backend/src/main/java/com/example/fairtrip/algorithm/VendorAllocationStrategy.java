package com.example.fairtrip.algorithm;

import java.util.Optional;

/**
 * Strategy interface for vendor allocation.
 * Follows Strategy Pattern and Open-Closed Principle to allow pluggable allocation logic.
 */
public interface VendorAllocationStrategy {

    /**
     * Evaluates candidate vendors against contractual targets and operational constraints,
     * deterministically selecting the winning vendor.
     *
     * @param context the complete context including trip, candidates, targets, historical allocations, and cooldowns
     * @return the detailed allocation result
     */
    AllocationResult selectVendor(AllocationContext context);
}
