package com.globallogic.bci.dto;

/**
 * Data Transfer Object for error response.
 * Contains error details including timestamp, code, and message.
 */
public class ErrorDetail {
    private String timestamp;
    private Integer codigo;
    private String detail;

    public ErrorDetail() {
    }

    public ErrorDetail(String timestamp, Integer codigo, String detail) {
        this.timestamp = timestamp;
        this.codigo = codigo;
        this.detail = detail;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
