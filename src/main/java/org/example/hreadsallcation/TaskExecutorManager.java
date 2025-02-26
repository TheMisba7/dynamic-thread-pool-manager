package org.example.hreadsallcation;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface TaskExecutorManager {
    void activateService(ServiceType serviceType);
    void inactivateService(ServiceType serviceType);

    void exec(ServiceType serviceType, Runnable task);
    <T> Future<T> submit(ServiceType serviceType, Callable<T> callable);
}
