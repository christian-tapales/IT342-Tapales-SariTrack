package edu.cit.tapales.saritrack.feature.customer.repository;

import edu.cit.tapales.saritrack.feature.customer.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void testFindByVendorId_ShouldReturnCustomerList() {
        // Arrange
        Customer c1 = new Customer();
        c1.setVendorId(1L);
        c1.setFullName("Juan");
        
        Customer c2 = new Customer();
        c2.setVendorId(1L);
        c2.setFullName("Maria");
        
        Customer c3 = new Customer();
        c3.setVendorId(2L);
        c3.setFullName("Pedro");
        
        customerRepository.saveAll(List.of(c1, c2, c3));

        // Act
        List<Customer> vendor1Customers = customerRepository.findByVendorId(1L);

        // Assert
        assertEquals(2, vendor1Customers.size());
    }
}
