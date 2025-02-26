package org.example.hreadsallcation;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskExecutorManagerImpl implements TaskExecutorManager {
    private final ExecutorService executorService;
    private final Map<ServiceType, Boolean> activeServices = new ConcurrentHashMap<>();
    private final Map<ServiceType, Semaphore> serviceSemaphores = new ConcurrentHashMap<>();
    public TaskExecutorManagerImpl() {
        executorService = new ThreadPoolExecutor(
                0, ServiceType.getCount(),
                30, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        for (ServiceType serviceType: ServiceType.values()) {
            serviceSemaphores.put(serviceType, new Semaphore(serviceType.getMinThreads()));
            activeServices.put(serviceType, false);
        }
    }
    @Override
    public void activateService(ServiceType serviceType) {
        activeServices.replace(serviceType, true);
    }

    @Override
    public void inactivateService(ServiceType serviceType) {
        activeServices.replace(serviceType, false);
    }

    @Override
    public void exec(ServiceType serviceType, Runnable task) {
        if (!activeServices.get(serviceType))
            throw new RuntimeException(serviceType + " is inactive. Please activate it first.");

        Semaphore servicePermits = acquirePermit(serviceType);

        executorService.execute(() -> {
            try {
                task.run();
            } finally {
                servicePermits.release();
            }
        });
    }

    @Override
    public <T> Future<T> submit(ServiceType serviceType, Callable<T> callable) {
        if (!activeServices.get(serviceType))
            throw new RuntimeException(serviceType + " is inactive. Please activate it first.");

        Semaphore servicePermits = acquirePermit(serviceType);
        return executorService.submit(() -> {
            try {
                return callable.call();
            } finally {
                servicePermits.release();
            }
        });
    }

    private Semaphore acquirePermit(ServiceType serviceType) {
        Semaphore servicePermits = serviceSemaphores.get(serviceType);
        while (true) {
            if (servicePermits.tryAcquire()) {
                return servicePermits;
            } else {
                Optional<ServiceType> inactiveService = activeServices.entrySet().stream()
                        .filter(entry -> !entry.getValue())
                        .map(Map.Entry::getKey)
                        .filter(s -> serviceSemaphores.get(s).availablePermits() > 0)
                        .findFirst();
                if (inactiveService.isPresent()) {
                    Semaphore semaphore = serviceSemaphores.get(inactiveService.get());
                    if (semaphore.tryAcquire())
                        return semaphore;
                }
            }
        }
    }
}
