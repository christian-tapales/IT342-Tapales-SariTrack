package edu.cit.tapales.saritrack.controller;

import edu.cit.tapales.saritrack.entity.Customer;
import edu.cit.tapales.saritrack.entity.DebtPayment;
import edu.cit.tapales.saritrack.repository.CustomerRepository;
import edu.cit.tapales.saritrack.repository.DebtPaymentRepository;
import edu.cit.tapales.saritrack.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DebtPaymentRepository debtPaymentRepository;

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public List<Customer> getCustomers(@RequestParam Long vendorId) {
        return customerRepository.findByVendorId(vendorId);
    }

    @PostMapping
    public Customer addCustomer(@RequestBody Customer customer) {
        if (customer.getCurrentDebt() == null) customer.setCurrentDebt(0.0);
        customer.setLastUpdate(LocalDateTime.now());
        return customerRepository.save(customer);
    }

    @PostMapping("/{id}/pay")
    public Customer recordPayment(@PathVariable Long id, @RequestBody Map<String, Double> payload) {
        Double amount = payload.get("amount");
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        double newDebt = Math.max(0, customer.getCurrentDebt() - amount);
        customer.setCurrentDebt(newDebt);
        customer.setLastUpdate(LocalDateTime.now());
        customer.setStatus(newDebt > 0 ? "Partial" : "Paid");
        Customer savedCustomer = customerRepository.save(customer);

        DebtPayment payment = new DebtPayment();
        payment.setCustomerId(id);
        payment.setAmount(amount);
        payment.setTimestamp(LocalDateTime.now());
        debtPaymentRepository.save(payment);

        notificationService.createNotification(
            customer.getVendorId(), 
            "Payment Received!", 
            customer.getFullName() + " paid ₱" + amount + ". Remaining debt: ₱" + newDebt, 
            "SUCCESS"
        );
        
        return savedCustomer;
    }

    @GetMapping("/{id}/payments")
    public List<DebtPayment> getPaymentHistory(@PathVariable Long id) {
        return debtPaymentRepository.findByCustomerId(id).stream()
                .sorted(Comparator.comparing(DebtPayment::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        customerRepository.deleteById(id);
    }
}