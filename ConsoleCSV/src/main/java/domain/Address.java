package domain;

import java.util.Objects;

public final class Address {

    private final String city;
    private final String street;
    private final int house;
    private final int floor;

    public Address(String city, String street, int house, int floor) {
        this.city = Objects.requireNonNull(city, "city");
        this.street = Objects.requireNonNull(street, "street");
        this.house = house;
        this.floor = floor;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public int getHouse() {
        return house;
    }

    public int getFloor() {
        return floor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Address other)) {
            return false;
        }
        return house == other.house
                && floor == other.floor
                && city.equals(other.city)
                && street.equals(other.street);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, street, house, floor);
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %d, %d", city, street, house, floor);
    }
}
