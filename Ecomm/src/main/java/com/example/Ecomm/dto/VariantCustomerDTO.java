package com.example.Ecomm.dto;

public class VariantCustomerDTO {

    private Long id;
    private String size;
    private boolean available;

    public VariantCustomerDTO() {
    }

    public VariantCustomerDTO(Long id, String size, boolean available) {
        this.id = id;
        this.size = size;
        this.available = available;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
