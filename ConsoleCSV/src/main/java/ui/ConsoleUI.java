package ui;

import domain.Address;
import parser.AddressParser;
import parser.ParserFactory;
import statistics.StatisticsCalculator;
import statistics.StatisticsResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public final class ConsoleUI {

    private static final String EXIT_COMMAND = "exit";
    private static final String QUIT_COMMAND = "quit";
    private static final String Q_COMMAND = "q";
    private static final String EXIT_COMMAND_RU = "выход";

    private final Scanner scanner;
    private final StatisticsCalculator statisticsCalculator;

    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
        this.statisticsCalculator = new StatisticsCalculator();
    }

    public void run() {
        printWelcome();
        while (true) {
            System.out.print("> ");
            String input = readLine();
            if (input == null) {
                System.out.println();
                System.out.println("Завершение работы.");
                return;
            }
            input = input.trim();
            if (input.isEmpty()) {
                continue;
            }
            if (isExitCommand(input)) {
                System.out.println("Завершение работы.");
                return;
            }
            processInput(input);
        }
    }

    private void printWelcome() {
        System.out.println("=== Анализ справочника адресов ===");
        System.out.println("Введите путь к файлу .csv или .xml.");
        System.out.println("Для выхода введите одну из команд: "
                + EXIT_COMMAND + ", " + QUIT_COMMAND + ", " + Q_COMMAND + ", " + EXIT_COMMAND_RU + ".");
        System.out.println();
    }

    private String readLine() {
        if (scanner.hasNextLine()) {
            return scanner.nextLine();
        }
        return null;
    }

    private boolean isExitCommand(String input) {
        String normalized = input.trim().toLowerCase(Locale.ROOT);
        return normalized.equals(EXIT_COMMAND)
                || normalized.equals(QUIT_COMMAND)
                || normalized.equals(Q_COMMAND)
                || normalized.equals(EXIT_COMMAND_RU);
    }

    private void processInput(String input) {
        Path path;
        try {
            path = parsePath(input);
        } catch (InvalidPathException e) {
            System.err.println("Некорректный путь: " + input);
            return;
        }
        processFile(path);
    }

    private Path parsePath(String raw) {
        String value = raw.trim();
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        return Path.of(value);
    }

    private void processFile(Path path) {
        if (!Files.exists(path)) {
            System.err.println("Файл не найден: " + path.toAbsolutePath());
            return;
        }
        if (!Files.isRegularFile(path)) {
            System.err.println("Указанный путь не является файлом: " + path.toAbsolutePath());
            return;
        }
        if (!Files.isReadable(path)) {
            System.err.println("Нет прав на чтение файла: " + path.toAbsolutePath());
            return;
        }

        try {
            long startNanos = System.nanoTime();
            AddressParser parser = ParserFactory.forPath(path);
            List<Address> records = parser.parse(path);
            long parsingNanos = System.nanoTime() - startNanos;

            StatisticsResult result = statisticsCalculator.calculate(records, parsingNanos);
            printStatistics(result);
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Непредвиденная ошибка при обработке файла: " + e.getMessage());
        }
    }

    private void printStatistics(StatisticsResult result) {
        System.out.println();
        System.out.println("--- Статистика ---");
        System.out.println("Всего записей: " + result.totalRecords());

        printDuplicates(result);
        printFloorDistribution(result);

        System.out.printf("Время обработки файла: %.3f мс%n", result.processingMillis());
        System.out.println();
    }

    private void printDuplicates(StatisticsResult result) {
        List<Map.Entry<Address, Integer>> duplicates = result.duplicates();
        if (duplicates.isEmpty()) {
            System.out.println("Дублирующиеся записи: нет.");
            return;
        }
        System.out.println("Дублирующиеся записи:");
        for (Map.Entry<Address, Integer> entry : duplicates) {
            System.out.printf("  %s — %d раз(а)%n", entry.getKey(), entry.getValue());
        }
    }

    private void printFloorDistribution(StatisticsResult result) {
        Map<String, Map<Integer, Integer>> distribution = result.floorDistribution();
        if (distribution.isEmpty()) {
            System.out.println("Нет данных по этажности для городов.");
            return;
        }
        System.out.println("Распределение зданий по этажам в городах:");
        for (Map.Entry<String, Map<Integer, Integer>> cityEntry : distribution.entrySet()) {
            String city = cityEntry.getKey();
            Map<Integer, Integer> floors = cityEntry.getValue();
            StringBuilder line = new StringBuilder("  ").append(city).append(": ");
            for (int floor = 1; floor <= 5; floor++) {
                int count = floors.getOrDefault(floor, 0);
                line.append(floor).append(" эт. — ").append(count).append("; ");
            }

            line.setLength(line.length() - 2);
            System.out.println(line);
        }
    }
}