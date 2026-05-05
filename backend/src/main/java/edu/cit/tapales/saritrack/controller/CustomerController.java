package edu.cit.tapales.saritrack.controller;

import edu.cit.tapales.saritrack.entity.Customer;
import edu.cit.tapales.saritrack.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

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
        
        return customerRepository.save(customer);
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        customerRepository.deleteById(id);
    }
}