package parser;

import java.nio.file.Path;
import java.util.Locale;

public final class ParserFactory {
    public static AddressParser forPath(Path filePath) {
        String name = filePath.getFileName().toString().toLowerCase(Locale.ROOT);
        if (name.endsWith(".csv")) {
            return new CsvAddressParser();
        }
        if (name.endsWith(".xml")) {
            return new XmlAddressParser();
        }
        throw new IllegalArgumentException(
                "Неподдерживаемый формат файла: " + filePath.getFileName()
                        + ". Ожидаются файлы .csv или .xml");
    }
}
