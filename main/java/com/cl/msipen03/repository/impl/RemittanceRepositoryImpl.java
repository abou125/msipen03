package com.cl.msipen03.repository.impl;

import com.cl.logs.commun.CommonLoggerFactory;
import com.cl.msipen03.entities.RemittanceOperation;
import com.cl.msipen03.entities.RemittanceOrder;
import com.cl.msipen03.mapper.RemittanceOperationsMapper;
import com.cl.msipen03.mapper.RemittanceOrderMapper;
import com.cl.msipen03.repository.RemittanceRepository;
import com.cl.msipen03.service.RemittanceQueryService;
import org.slf4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class RemittanceRepositoryImpl implements RemittanceRepository {


    private final JdbcTemplate jdbcTemplate;

    private static final Logger LOGGER = CommonLoggerFactory.getLogger(RemittanceQueryService.class).logger();

    private static final String SQL_STATUT_DE_NIVEAU_REMISE = "select * from virinst1a.fct_feedback_masse_restitution_statut_niveau_ordre(?)";

    private static final String SQL_STATUT_DE_NIVEAU_OPERATION = "select * from virinst1a.fct_feedback_masse_restitution_statut_niveau_operation(?,?,?)";


    public RemittanceRepositoryImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public Optional<RemittanceOrder> findByRemittanceOrderId(String orderId) {
        LOGGER.info("Execute query - RemittanceDatabaseRespository: fct_feedback_masse_restitution_statut_niveau_remise:");

        try {
            RemittanceOrder result = jdbcTemplate.queryForObject(SQL_STATUT_DE_NIVEAU_REMISE,
                    new RemittanceOrderMapper(), orderId);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            LOGGER.info("Remittance Order Id not found in Database: {}", orderId, e);
            return Optional.empty();
        }
    }




    public Page<RemittanceOperation> findOperationsByRemittanceOrderId(String orderId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        LOGGER.info("Execute query - RemittanceDatabaseRespository: fct_feedback_masse_restitution_statut_niveau_operation");

        List<RemittanceOperation> remittanceOperationList = jdbcTemplate.query(SQL_STATUT_DE_NIVEAU_OPERATION, new RemittanceOperationsMapper(),
                orderId, page, size);

        long totalCount = extractTotalCount(remittanceOperationList);

        LOGGER.info("Execute query - fct_Remittance_total_operation: total: {} ", totalCount);

        return new PageImpl<>(remittanceOperationList, pageable, totalCount);
    }

    private long extractTotalCount(List<RemittanceOperation> operations) {
        return operations.isEmpty() ? 0 : operations.get(0).totalCount();
    }


}
