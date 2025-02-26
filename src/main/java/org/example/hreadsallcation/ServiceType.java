package org.example.hreadsallcation;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ServiceType {
    BILLS(6),
    INVOICES(6),
    PAYMENT(1),
    OTHER(1);

    private final int minThreads;

    ServiceType(int minThreads) {
        this.minThreads = minThreads;
    }

    public static int getCount() {
        return Arrays.stream(ServiceType.values()).mapToInt(ServiceType::getMinThreads).sum();
    }
}
