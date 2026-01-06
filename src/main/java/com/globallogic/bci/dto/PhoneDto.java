package com.globallogic.bci.dto;

/**
 * Data Transfer Object for phone information.
 * Used to transfer phone data between API and service layers.
 */
public class PhoneDto {
    private Long number;
    private Integer citycode;
    private String contrycode;

    public PhoneDto() {
    }

    public PhoneDto(Long number, Integer citycode, String contrycode) {
        this.number = number;
        this.citycode = citycode;
        this.contrycode = contrycode;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public Integer getCitycode() {
        return citycode;
    }

    public void setCitycode(Integer citycode) {
        this.citycode = citycode;
    }

    public String getContrycode() {
        return contrycode;
    }

    public void setContrycode(String contrycode) {
        this.contrycode = contrycode;
    }
}
