package com.example.blog.entity;

import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class Address {

    private String street;
    private String city;
    private String district;
    private String zipCode;
    private String country;

    public Address() {
    }

    public Address(String street, String city, String district, String zipCode, String country) {
        this.street = street;
        this.city = city;
        this.district = district;
        this.zipCode = zipCode;
        this.country = country;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(street, address.street) && Objects.equals(city, address.city) && Objects.equals(district, address.district) && Objects.equals(zipCode, address.zipCode) && Objects.equals(country, address.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, district, zipCode, country);
    }
}
