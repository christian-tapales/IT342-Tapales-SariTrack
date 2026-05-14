package edu.cit.tapales.saritrack.core.logic;

import edu.cit.tapales.saritrack.feature.order.entity.OrderItem;
import edu.cit.tapales.saritrack.feature.notification.entity.Notification;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BoundaryVerificationTest {

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 0.01, 999999.99, 123.456})
    void testOrderItemPriceBoundaries(double price) {
        OrderItem item = new OrderItem();
        item.setPriceAtSale(price);
        assertEquals(price, item.getPriceAtSale());
    }

    @ParameterizedTest
    @ValueSource(strings = {"INFO", "WARNING", "SUCCESS", "ERROR", "CRITICAL", "DEBUG", "TRACE"})
    void testNotificationTypeBoundaries(String type) {
        Notification n = new Notification();
        n.setType(type);
        assertEquals(type, n.getType());
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 999L, 1000000L, 0L, -1L})
    void testNotificationVendorIdBoundaries(long id) {
        Notification n = new Notification();
        n.setVendorId(id);
        assertEquals(id, n.getVendorId());
    }
}
