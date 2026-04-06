package edu.cit.tapales.saritrack.repository;
import edu.cit.tapales.saritrack.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {}