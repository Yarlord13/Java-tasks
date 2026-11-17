package parser;

import domain.Address;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface AddressParser {
    List<Address> parse(Path filePath) throws IOException;
}
