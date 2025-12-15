package com.projekt.magisterski.backend.energy.model;

import com.projekt.magisterski.backend.energy.repo.CustomerRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class CustomerEnergyUsage implements ApplicationListener<ContextRefreshedEvent> {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Iterable<Customer> users = customerRepository.findAll();
        for (Customer customer : users) {
            createSecondTableIfNotExists(customer.getPPE());
        }
    }

    @Transactional
    public void handleCustomerAddedEvent(Customer customer) {
        createSecondTableIfNotExists(customer.getPPE());
    }

    private void createSecondTableIfNotExists(String PPE) {
        String tableName = "energyusage_" + PPE;

        if (!isTableExists(tableName)) {
            String createTableSql = String.format(
                    "CREATE TABLE %s (" +
                            "id INT PRIMARY KEY AUTO_INCREMENT," +
                            "time TIMESTAMP," +
                            "a_plus DOUBLE," +
                            "a_minus DOUBLE" +
                            ")", tableName);

            entityManager.createNativeQuery(createTableSql).executeUpdate();
            System.out.println("Pozytywnie dodano tabelę dla:" + PPE);
        }
    }

    public boolean isTableExists(String tableName) {
        Optional<?> result = entityManager
                .createNativeQuery("SHOW TABLES LIKE :tableName")
                .setParameter("tableName", tableName)
                .getResultList()
                .stream()
                .findFirst();

        return result.isPresent();
    }

    @Transactional
    public List<Object[]> getDataFromTable(String tableName) {
        if (isTableExists(tableName)) {
            String selectDataSql = String.format("SELECT * FROM %s", tableName);
            return entityManager.createNativeQuery(selectDataSql).getResultList();
        } else {
            throw new IllegalArgumentException("Tabela o nazwie " + tableName + " nie istnieje.");
        }
    }

    @Transactional
    public List<Object[]> getInvoicesByCurrentUser(String customerId) {
        String selectDataSql = String.format("SELECT * FROM invoices WHERE user_id = %s", customerId);
        return entityManager.createNativeQuery(selectDataSql).getResultList();

    }

    @Transactional
    public List<Object[]> getDataFromTableWithTimeRange(String tableName, String startTime, String endTime) {
        if (isTableExists(tableName)) {
            String selectDataSql = String.format("SELECT ROUND(SUM(a_plus), 0) AS a_plus, ROUND(SUM(a_minus), 0) AS a_minus FROM %s WHERE time >= '%s' AND time <= '%s'", tableName, startTime, endTime);
            List<Object[]> resultList = entityManager.createNativeQuery(selectDataSql).getResultList();

            for (Object[] row : resultList) {
                if (row[0] == null && row[1] == null) {
                    throw new IllegalArgumentException("Błędne dane");
                }
            }
            return resultList;
        } else {
            throw new IllegalArgumentException("Tabela o nazwie " + tableName + " nie istnieje.");
        }
    }

    public List<Object[]> getCustomerSumWithRange(Customer customer, String startTime, String endTime) {
        String tableName = "energyusage_" + customer.getPPE();
        List<Object[]> resultList = getDataFromTableWithTimeRange(tableName, startTime, endTime);
        return resultList;
    }

    @Transactional
    public List<Object[]> getAllCurrentUserDataWithTimeRange(Customer customer, String startTime, String endTime) {
        String tableName = "energyusage_" + customer.getPPE();
        if (isTableExists(tableName)) {
            String selectDataSql = String.format("SELECT * FROM %s WHERE time >= '%s' AND time <= '%s'", tableName, startTime, endTime);
            List<Object[]> resultList = entityManager.createNativeQuery(selectDataSql).getResultList();

            for (Object[] row : resultList) {
                if (row[0] == null && row[1] == null) {
                    throw new IllegalArgumentException("Błędne dane");
                }
            }
            return resultList;
        } else {
            throw new IllegalArgumentException("Tabela o nazwie " + tableName + " nie istnieje.");
        }
    }

    public List<List<Object[]>> getCurrentUserHoursSumWithTimeRange(Customer customer, String startTime, String endTime) {
        Hours energyUsageMidnightToSix = new Hours(0, 5);
        Hours energyUsageSixToTwelve = new Hours(6, 11);
        Hours energyUsageTwelveToEighteen = new Hours(12, 17);
        Hours energyUsageEighteenToMidnight = new Hours(18, 24);
        List<Hours> listHours = new ArrayList<>();
        listHours.add(energyUsageMidnightToSix);
        listHours.add(energyUsageSixToTwelve);
        listHours.add(energyUsageTwelveToEighteen);
        listHours.add(energyUsageEighteenToMidnight);

        List<List<Object[]>> listOfAllObjects = new ArrayList<>();
        for (Hours element : listHours) {
            List<Object[]> listOfObject = getCurrentUserHoursSumWithTimeRange(customer, startTime, endTime, element.getHourStart(), element.getHourEnd());
            listOfAllObjects.add(listOfObject);
        }
        return listOfAllObjects;
    }

    @Transactional
    public List<Object[]> getCurrentUserHoursSumWithTimeRange(Customer customer, String startTime, String endTime, int hourStart, int hourEnd) {
        String tableName = "energyusage_" + customer.getPPE();
        if (isTableExists(tableName)) {
            String selectDataSql = String.format(
                    "SELECT " +
                            "SUM(a_plus) AS sum_a_plus, " +
                            "SUM(a_minus) AS sum_a_minus " +
                            "FROM %s " +
                            "WHERE " +
                            "time >= '%s' AND " +
                            "time <= '%s' AND " +
                            "EXTRACT(HOUR FROM time) >= %d AND " +
                            "EXTRACT(HOUR FROM time) <= %d", tableName, startTime, endTime, hourStart, hourEnd
            );

            List<Object[]> resultList = entityManager.createNativeQuery(selectDataSql).getResultList();

            for (Object[] row : resultList) {
                if (row[0] == null && row[1] == null) {
                    throw new IllegalArgumentException("Błędne dane");
                }
            }
            return resultList;
        } else {
            throw new IllegalArgumentException("Tabela o nazwie " + tableName + " nie istnieje.");
        }
    }

    @Transactional
    public List<Object[]> getSingleEnergyUsage(Long singleMeasureId, String PPE) {
        String tableName = "energyusage_" + PPE;
        if (isTableExists(tableName)) {
            String selectDataSql = String.format("SELECT * FROM %s WHERE id = '%s'", tableName, singleMeasureId);
            List<Object[]> resultList = entityManager.createNativeQuery(selectDataSql).getResultList();

            for (Object[] row : resultList) {
                if (row[0] == null && row[1] == null) {
                    throw new IllegalArgumentException("Błędne dane");
                }
            }
            return resultList;
        } else {
            throw new IllegalArgumentException("Tabela o nazwie " + tableName + " nie istnieje.");
        }
    }

    @Transactional
    public boolean updateSingleMeasure(String PPE, String a_plus, String a_minus, String singleMeasureId) {
        String tableName = "energyusage_" + PPE;
        if (isTableExists(tableName)) {
            String selectDataSql = String.format("UPDATE %s SET a_plus = '%s', a_minus = '%s'  WHERE id = '%s' ", tableName, a_plus, a_minus, singleMeasureId);
            Query query = entityManager.createNativeQuery(selectDataSql);
            int updatedRows = query.executeUpdate();
            return true;
        } else {
            return false;
        }
    }

}