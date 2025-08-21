package com.cl.msipen03.repository;

import com.cl.msipen03.entities.RemittanceOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RemittanceOrderRepository extends JpaRepository<RemittanceOrder, Integer> {

    @Query(nativeQuery = true, value = "select * from virinst1a.fct_feedback_masse_restitution_statut_niveau_ordre(:orderId)")
    Optional<RemittanceOrder> findByRemittanceOrderId(@Param("orderId") String orderId);

}



