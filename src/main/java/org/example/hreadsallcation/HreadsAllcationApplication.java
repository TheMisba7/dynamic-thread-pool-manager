package org.example.hreadsallcation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

//@SpringBootApplication
public class HreadsAllcationApplication {

    public static void main(String[] args) {
        //SpringApplication.run(HreadsAllcationApplication.class, args);
        TaskExecutorManager taskExecutorManager = new TaskExecutorManagerImpl();

        taskExecutorManager.activateService(ServiceType.BILLS);
        taskExecutorManager.activateService(ServiceType.INVOICES);
        taskExecutorManager.activateService(ServiceType.OTHER);
        taskExecutorManager.activateService(ServiceType.PAYMENT);

        System.out.println("counter: " + ServiceType.getCount());
        List<Future<String>> futures = new ArrayList<>();
        for (int i = 1; i < 10000; i++) {
            int finalI = i;
//            taskExecutorManager.exec(ServiceType.PAYMENT, () -> {
//
//                System.out.println("processing item: " + (finalI + 1));
//                try {
//                    Thread.sleep(10000);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//                System.out.println("done processing item: " + (finalI + 1));
//            });

            futures.add(taskExecutorManager.submit(ServiceType.BILLS, () -> {
                System.out.println("sleeping..." + finalI);
                Thread.sleep(10000);
                return "hey #-" + finalI;
            }));
        }
        for (Future<String> future: futures) {
            try {
                System.out.println(future.get());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
