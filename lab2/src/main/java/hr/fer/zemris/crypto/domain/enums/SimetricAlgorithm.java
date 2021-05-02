package hr.fer.zemris.crypto.domain.enums;

public enum SimetricAlgorithm {
    AES("AES", "AES"),
    DES("DES", "DES"),
    DESede("3DES", "DESede");

    private final String name;
    private final String code;

    SimetricAlgorithm(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
