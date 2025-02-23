package org.example.hreadsallcation;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class TaskExecutorManagerImpl implements TaskExecutorManager {
    private final ExecutorService executorService;
    private final Map<ServiceType, Boolean> activeServices = new ConcurrentHashMap<>();
    private final Map<ServiceType, Semaphore> serviceSemaphores = new ConcurrentHashMap<>();
    public TaskExecutorManagerImpl() {
        this.executorService = Executors.newFixedThreadPool(14);
        for (ServiceType serviceType: ServiceType.values()) {
            serviceSemaphores.put(serviceType, new Semaphore(serviceType.getMinThreads()));
            activeServices.put(serviceType, false);
        }
    }
    @Override
    public void activateService(ServiceType serviceType) {
        activeServices.put(serviceType, true);
    }

    @Override
    public void inactivateService(ServiceType serviceType) {
        activeServices.put(serviceType, false);
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

    private Semaphore acquirePermit(ServiceType serviceType) {
        Semaphore servicePermits = serviceSemaphores.get(serviceType);
        boolean permission;
        while (true) {
            permission = servicePermits.tryAcquire();
            if (permission) {
                return servicePermits;
            } else {
                Optional<ServiceType> inactiveService = activeServices.entrySet().stream()
                        .filter(entry -> !entry.getValue()) // Find inactive services
                        .map(Map.Entry::getKey)
                        .filter(s -> serviceSemaphores.get(s).availablePermits() > 0)
                        .findFirst();
                if (inactiveService.isPresent()) {
                    Semaphore semaphore = serviceSemaphores.get(inactiveService.get());
                    boolean b = semaphore.tryAcquire();
                    if (b)
                        return semaphore;
                }
            }
        }
    }
}
