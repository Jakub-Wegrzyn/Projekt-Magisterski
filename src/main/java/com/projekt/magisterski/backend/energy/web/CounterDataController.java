package com.projekt.magisterski.backend.energy.web;

import com.projekt.magisterski.backend.energy.model.Customer;
import com.projekt.magisterski.backend.energy.model.CustomerEnergyUsage;
import com.projekt.magisterski.backend.energy.repo.CustomerRepository;
import com.projekt.magisterski.backend.energy.web.dto.EnergyUsageDto;
import com.projekt.magisterski.backend.energy.web.dto.SumEnergyUsageDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/counterdata")
public class CounterDataController {

    private CustomerRepository customerRepository;
    private CustomerEnergyUsage customerEnergyUsage;

    public CounterDataController(CustomerRepository customerRepository, CustomerEnergyUsage customerEnergyUsage) {
        this.customerRepository = customerRepository;
        this.customerEnergyUsage = customerEnergyUsage;
    }

    @GetMapping
    public String counterData(Model model)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Customer customer = customerRepository.findByEmail(email);
        model.addAttribute(customer);
        return "counterdata";
    }

    @PostMapping("/search")
    public ResponseEntity<List<EnergyUsageDto>> getTableData(@RequestParam String startDate,
                               @RequestParam String endDate,
                               Model model)
    {
        List<EnergyUsageDto> energyUsageList = new ArrayList<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Customer customer = customerRepository.findByEmail(email);
        List<Object[]> dataList =  customerEnergyUsage.getAllCurrentUserDataWithTimeRange(customer, startDate, endDate);
        if (!dataList.isEmpty()) {
            for(Object[] row : dataList){
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

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/searchammount")
    public ResponseEntity<List<SumEnergyUsageDto>> getAmmount(@RequestParam String startDate,
                                                             @RequestParam String endDate,
                                                             Model model)
    {
        List<SumEnergyUsageDto> sumEnergyUsageList = new ArrayList<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Customer customer = customerRepository.findByEmail(email);
        List<Object[]> dataAmmountOfEnergy =  customerEnergyUsage.getCustomerSumWithRange(customer, startDate, endDate);
        if (!dataAmmountOfEnergy.isEmpty()) {
            Double suma_a_plus = 0.0;
            Double suma_a_minus = 0.0;
            if(!dataAmmountOfEnergy.isEmpty()){
                for(Object[] row : dataAmmountOfEnergy){
                    suma_a_plus = (Double) row[0];
                    suma_a_minus = (Double) row[1];
                }
            }
            SumEnergyUsageDto sumEnergyUsageDto = new SumEnergyUsageDto(suma_a_plus, suma_a_minus);
            sumEnergyUsageList.add(sumEnergyUsageDto);

            model.addAttribute("sumEnergyUsageList", sumEnergyUsageList);
            return ResponseEntity.ok(sumEnergyUsageList);
        }

        return ResponseEntity.notFound().build();
    }
}
