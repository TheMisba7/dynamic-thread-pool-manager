# Problem: Dynamic Thread Pool Management Across Multiple Services
We have an ExecutorService with a fixed pool of 14 threads that needs to be shared among four business services:

- BillService
- InvoiceService
- PaymentService
- OtherService

<h4>Thread Allocation Requirements:</h4>

BillService and InvoiceService together require 12 threads minimum (6 threads each)
PaymentService needs 1 thread minimum
VCCPaymentService needs 1 thread minimum

Dynamic Allocation Rules:

When all services are running, each should get their minimum required threads
When some services are inactive, their allocated threads should be made available to active services
Example: If InvoiceService is not running, its 6 threads should be distributed among other active services
When a previously inactive service becomes active (e.g., InvoiceService starts running), it should reclaim its minimum thread allocation