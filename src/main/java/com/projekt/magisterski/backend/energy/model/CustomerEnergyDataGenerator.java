package com.projekt.magisterski.backend.energy.model;

import com.projekt.magisterski.backend.energy.repo.CustomerRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;

@Component
public class CustomerEnergyDataGenerator {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    @Transactional
    @Scheduled(fixedRate = 900000)
    public void generateDataForAllTables() {
        Iterable<Customer> users = customerRepository.findAll();
        for (Customer customer : users) {
            generateAndSaveRandomData(customer.getPPE(), customer.isProsument());
        }
    }

    @Transactional
    public void generateAndSaveRandomData(String PPE, boolean isProsument) {
        String tableName = "energyusage_" + PPE;

        if (isTableExists(tableName)) {
            String insertDataSql = String.format(
                    "INSERT INTO %s (time, a_plus, a_minus) VALUES (?, ?, ?)", tableName);
            System.out.println("Pozytywnie dodano dane dla: " + PPE);

            entityManager.createNativeQuery(insertDataSql)
                    .setParameter(1, LocalDateTime.now())
                    .setParameter(2, generateRandomA_plus())
                    .setParameter(3, generateRandomA_minus(LocalDateTime.now(), isProsument))
                    .executeUpdate();
        }
    }

    private double generateRandomA_plus() {
        double minValue = 0.07;
        double maxValue = 0.2;
        double randomValue = minValue + (maxValue - minValue) * new Random().nextDouble();

        return Math.round(randomValue * 10000.0) / 10000.0;
    }

    private double generateRandomA_minus(LocalDateTime localDateTime, boolean isProsument) {
        LocalTime currentTime = localDateTime.toLocalTime();
        if (isProsument) {
            if (currentTime.isAfter(LocalTime.of(6, 0)) && currentTime.isBefore(LocalTime.of(18, 0))) {
                double minValue = 0.2;
                double maxValue = 0.7;
                double randomValue = minValue + (maxValue - minValue) * new Random().nextDouble();
                return Math.round(randomValue * 10000.0) / 10000.0;
            } else {
                return 0.0;
            }
        } else {
            return 0.0;
        }

    }

    private boolean isTableExists(String tableName) {
        List<?> result = entityManager
                .createNativeQuery("SHOW TABLES LIKE :tableName")
                .setParameter("tableName", tableName)
                .getResultList();

        return !result.isEmpty();
    }
}
