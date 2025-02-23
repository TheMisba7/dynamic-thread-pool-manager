package org.example.hreadsallcation;

//@SpringBootApplication
public class HreadsAllcationApplication {

    public static void main(String[] args) {
        //SpringApplication.run(HreadsAllcationApplication.class, args);
        TaskExecutorManager taskExecutorManager = new TaskExecutorManagerImpl();

        taskExecutorManager.activateService(ServiceType.BILLS);
        taskExecutorManager.activateService(ServiceType.INVOICES);
        //taskEexcutorManager.activateService(ServiceType.PAYMENT);
        //taskEexcutorManager.activateService(ServiceType.OTHER);


        for (int i = 0; i < 1000; i++) {
            int finalI = i;
            taskExecutorManager.exec(ServiceType.INVOICES, () -> {

                System.out.println("processing item: " + (finalI + 1));
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }


                System.out.println("done processing item: " + (finalI + 1));
            });
        }

    }

}
