package com.projekt.magisterski.backend.energy.web;

import com.projekt.magisterski.backend.energy.model.Customer;
import com.projekt.magisterski.backend.energy.model.CustomerEnergyUsage;
import com.projekt.magisterski.backend.energy.repo.CustomerRepository;
import com.projekt.magisterski.backend.energy.web.dto.EnergyUsageDto;
import com.projekt.magisterski.backend.energy.web.dto.EnergyUsageDtoWithPPE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/odczyty")
public class OdczytyController {

    @Autowired
    private CustomerRepository customerRepository;
    private CustomerEnergyUsage customerEnergyUsage;

    public OdczytyController(CustomerRepository customerRepository, CustomerEnergyUsage customerEnergyUsage) {
        this.customerRepository = customerRepository;
        this.customerEnergyUsage = customerEnergyUsage;
    }

    @GetMapping
    public String odczyty(Model model) {
        List<Customer> customers = customerRepository.findAll();
        Map<Long, String> customerMap = new HashMap<>();
        for (Customer customer : customers) {
            String fullName = customer.getFirstName() + " " + customer.getLastName();
            customerMap.put(customer.getId(), fullName);
        }

        model.addAttribute("customerMap", customerMap);
        return "odczyty";
    }

    @GetMapping("/search")
    public String search() {
        return "odczyty";
    }


    @PostMapping("/search")
    public ResponseEntity<List<EnergyUsageDto>> searchCustomerById(@RequestParam Long customerId, Model model) {
        List<EnergyUsageDto> energyUsageList = new ArrayList<>();
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            String numerPPE = customer.getPPE();
            String tableName = "energyusage_" + numerPPE;

            List<Object[]> dataList = customerEnergyUsage.getDataFromTable(tableName);
            if (!dataList.isEmpty()) {
                for (Object[] row : dataList) {
                    Long id = ((Integer) row[0]).longValue();
                    Timestamp timestamp = (Timestamp) row[1];
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = sdf.format(timestamp);
                    Double aPlus = (Double) row[2];
                    Double aMinus = (Double) row[3];

                    EnergyUsageDto energyUsageDTO = new EnergyUsageDto(id, formattedDate, aPlus, aMinus);
                    energyUsageList.add(energyUsageDTO);
                }
                model.addAttribute("energyUsageList", energyUsageList);
                return ResponseEntity.ok(energyUsageList);
            }
        } else {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/correctmeasure")
    public ResponseEntity<String> correctMeasure(@RequestParam String singleMeasureId, String customerId) {
        Optional<Customer> customer = customerRepository.findById(Long.parseLong(customerId));
        String PPE = customer.get().getPPE();
        String redirectUrl = UriComponentsBuilder.fromPath("/odczyty/correctmeasure/{singleMeasureId}/{PPE}")
                .buildAndExpand(singleMeasureId, PPE)
                .toUriString();
        return ResponseEntity.ok().body(redirectUrl);
    }

    @GetMapping("/correctmeasure/{singleMeasureId}/{PPE}")
    public String getCorrectMeasure(@PathVariable Long singleMeasureId, @PathVariable String PPE, Model model) {
        List<Object[]> list = customerEnergyUsage.getSingleEnergyUsage(singleMeasureId, PPE);
        EnergyUsageDtoWithPPE energyUsageDtoWithPPE = null;
        for (Object[] row : list) {
            String id = ((Integer) row[0]).toString();
            Timestamp timestamp = (Timestamp) row[1];
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = sdf.format(timestamp);
            String aPlus = row[2].toString();
            String aMinus = row[3].toString();
            energyUsageDtoWithPPE = new EnergyUsageDtoWithPPE(id, PPE, formattedDate, aPlus, aMinus);
        }
        model.addAttribute("energyUsageDtoWithPPE", energyUsageDtoWithPPE);
        return "correctmeasure";
    }

    @PostMapping("/correctmeasure/{singleMeasureId}/{PPE}")
    public String updateMeasure(@ModelAttribute("energyUsageDtoWithPPE") EnergyUsageDtoWithPPE energyUsageDtoWithPPE) {
        customerEnergyUsage.updateSingleMeasure(energyUsageDtoWithPPE.getPPE(), energyUsageDtoWithPPE.getAPlus(), energyUsageDtoWithPPE.getAMinus(),
                energyUsageDtoWithPPE.getId());
        return "redirect:/odczyty/correctmeasure/{singleMeasureId}/{PPE}?success";
    }
}
