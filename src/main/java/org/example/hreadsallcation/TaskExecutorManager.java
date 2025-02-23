package org.example.hreadsallcation;

public interface TaskExecutorManager {
    void activateService(ServiceType serviceType);
    void inactivateService(ServiceType serviceType);

    void exec(ServiceType serviceType, Runnable task);
}
