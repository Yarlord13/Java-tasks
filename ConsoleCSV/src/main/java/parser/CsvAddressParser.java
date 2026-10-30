package parser;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import domain.Address;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class CsvAddressParser implements AddressParser {
    private static final char Separator = ';';
    private static final char Quote_char = '"';
    private static final int Expected_columns = 4;
    private static final int Header_lines = 1;

    @Override
    public List<Address> parse(Path filePath) throws IOException {
        List<Address> records = new ArrayList<>();

        CSVParser parser = new CSVParserBuilder()
                                .withSeparator(Separator)
                                .withQuoteChar(Quote_char)
                                .build();

        try (Reader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8);
             CSVReader csvReader = new CSVReaderBuilder(reader)
                                        .withCSVParser(parser)
                                        .withSkipLines(Header_lines)
                                        .build()) {

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                Address record = toRecord(line);
                if (record != null) {
                    records.add(record);
                }
            }
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
        return records;
    }

    private Address toRecord(String[] line) {
        if (line.length < Expected_columns) {
            return null;
        }
        try {
            String city = cleanField(line[0]);
            String street = cleanField(line[1]);
            int house = Integer.parseInt(cleanField(line[2]));
            int floor = Integer.parseInt(cleanField(line[3]));
            if (city.isEmpty() || street.isEmpty()) {
                return null;
            }
            return new Address(city, street, house, floor);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String cleanField(String raw) {
        String value = raw == null ? "" : raw.trim();
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        return value.trim();
    }
}
