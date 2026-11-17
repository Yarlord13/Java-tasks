package statistics;

import domain.Address;

import java.util.List;
import java.util.Map;

public record StatisticsResult(
        int totalRecords,
        List<Map.Entry<Address, Integer>> duplicates,
        Map<String, Map<Integer, Integer>> floorDistribution,
        long processingNanos) {

    public double processingMillis() {
        return processingNanos / 1_000_000.0;
    }
}