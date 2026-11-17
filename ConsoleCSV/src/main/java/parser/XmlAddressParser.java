package parser;

import domain.Address;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class XmlAddressParser implements AddressParser {

    private static final String Item_tag = "item";
    private static final String Attr_city = "city";
    private static final String Attr_street = "street";
    private static final String Attr_house = "house";
    private static final String Attr_floor = "floor";

    @Override
    public List<Address> parse(Path filePath) throws IOException {
        try (InputStream in = Files.newInputStream(filePath)) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            factory.setFeature(
                    "http://apache.org/xml/features/disallow-doctype-decl", true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(in);

            NodeList items = document.getElementsByTagName(Item_tag);
            List<Address> records = new ArrayList<>(items.getLength());
            for (int i = 0; i < items.getLength(); i++) {
                Element item = (Element) items.item(i);
                Address record = toRecord(item);
                if (record != null) {
                    records.add(record);
                }
            }
            return records;
        } catch (ParserConfigurationException | SAXException e) {
            throw new IOException("Не удалось разобрать XML-файл: " + filePath, e);
        }
    }

    private Address toRecord(Element item) {
        try {
            String city = item.getAttribute(Attr_city).trim();
            String street = item.getAttribute(Attr_street).trim();
            int house = Integer.parseInt(item.getAttribute(Attr_house).trim());
            int floor = Integer.parseInt(item.getAttribute(Attr_floor).trim());
            if (city.isEmpty() || street.isEmpty()) {
                return null;
            }
            return new Address(city, street, house, floor);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
