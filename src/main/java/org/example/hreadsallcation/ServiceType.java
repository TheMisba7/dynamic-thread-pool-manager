package org.example.hreadsallcation;

import lombok.Getter;

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
}
