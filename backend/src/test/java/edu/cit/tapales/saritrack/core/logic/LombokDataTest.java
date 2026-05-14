package edu.cit.tapales.saritrack.core.logic;

import edu.cit.tapales.saritrack.feature.product.entity.Product;
import edu.cit.tapales.saritrack.feature.auth.entity.User;
import edu.cit.tapales.saritrack.feature.customer.entity.Customer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class LombokDataTest {

    @ParameterizedTest
    @ValueSource(strings = {"Product A", "Product B", "Product C", "Special Item #1", "Coke 1.5L", "Pancit Canton", "Sardines Mega"})
    void testProductNameSetters(String name) {
        Product p = new Product();
        p.setName(name);
        assertEquals(name, p.getName());
    }

    @ParameterizedTest
    @ValueSource(strings = {"user1@gmail.com", "admin@saritrack.ph", "vendor.test@outlook.com", "customer@yahoo.com", "test.user.123@gmail.com"})
    void testUserEmailSetters(String email) {
        User u = new User();
        u.setEmail(email);
        assertEquals(email, u.getEmail());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 100.0, 9999.99, -50.0, 1.23, 100000.0, 0.0001})
    void testCustomerDebtSetters(double debt) {
        Customer c = new Customer();
        c.setCurrentDebt(debt);
        assertEquals(debt, c.getCurrentDebt());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, 1000, 50, 12, 999})
    void testProductStockSetters(int stock) {
        Product p = new Product();
        p.setStockQuantity(stock);
        assertEquals(stock, p.getStockQuantity());
    }
}
