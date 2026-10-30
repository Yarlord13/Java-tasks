package statistics;

import java.util.List;
import java.util.Map;

public record StatisticsResult<CityRecord>(
        int totalRecords,
        List<Map.Entry<CityRecord, Integer>> duplicates,
        Map<String, Map<Integer, Integer>> floorDistribution,
        long processingNanos) {

    public double processingMillis() {
        return processingNanos / 1_000_000.0;
    }
}