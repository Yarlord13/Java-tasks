package statistics;

import domain.Address;

import java.util.*;

public final class StatisticsCalculator {
    private static final int Min_floor = 1;
    private static final int Max_floor = 5;
    
    public StatisticsResult calculate(List<Address> records, long processingNanos) {
        List<Map.Entry<Address, Integer>> duplicates = findDuplicates(records);
        Map<String, Map<Integer, Integer>> floors = countFloorsByCity(records);
        return new StatisticsResult(records.size(), duplicates, floors, processingNanos);
    }
    
    private List<Map.Entry<Address, Integer>> findDuplicates(List<Address> records) {
        Map<Address, Integer> counter = new HashMap<>();
        for (Address record : records) {
            counter.merge(record, 1, Integer::sum);
        }
        List<Map.Entry<Address, Integer>> duplicates = new ArrayList<>();
        for (Map.Entry<Address, Integer> entry : counter.entrySet()) {
            if (entry.getValue() > 1) {
                duplicates.add(entry);
            }
        }
        duplicates.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        return duplicates;
    }
    
    private Map<String, Map<Integer, Integer>> countFloorsByCity(List<Address> records) {
        Map<String, Map<Integer, Integer>> result = new TreeMap<>();
        for (Address record : records) {
            int floor = record.getFloor();
            if (floor < Min_floor || floor > Max_floor) {
                continue;
            }
            Map<Integer, Integer> cityFloors = result.computeIfAbsent(
                    record.getCity(), city -> createEmptyFloorMap());
            cityFloors.merge(floor, 1, Integer::sum);
        }
        return result;
    }

    private static Map<Integer, Integer> createEmptyFloorMap() {
        Map<Integer, Integer> floors = new TreeMap<>();
        for (int i = Min_floor; i <= Max_floor; i++) {
            floors.put(i, 0);
        }
        return floors;
    }
}
