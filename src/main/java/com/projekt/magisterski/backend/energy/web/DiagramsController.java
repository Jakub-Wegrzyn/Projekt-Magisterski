package com.projekt.magisterski.backend.energy.web;

import com.projekt.magisterski.backend.energy.model.Customer;
import com.projekt.magisterski.backend.energy.model.CustomerEnergyUsage;
import com.projekt.magisterski.backend.energy.repo.CustomerRepository;
import com.projekt.magisterski.backend.energy.web.dto.CustomerEnergyUsageForChart;
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

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/diagrams")
public class DiagramsController {

    private CustomerRepository customerRepository;
    private CustomerEnergyUsage customerEnergyUsage;

    public DiagramsController(CustomerRepository customerRepository, CustomerEnergyUsage customerEnergyUsage) {
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
        return "diagrams";
    }

    @PostMapping("/search")
    public ResponseEntity<List<CustomerEnergyUsageForChart>> getTableData(@RequestParam String startDate,
                                                                          @RequestParam String endDate,
                                                                          Model model)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Customer customer = customerRepository.findByEmail(email);
        List<List<Object[]>> listOfAllObjects = customerEnergyUsage.getCurrentUserHoursSumWithTimeRange(customer, startDate, endDate);
        List<CustomerEnergyUsageForChart> customerEnergyUsageForChartList = new ArrayList<>();
        for(List<Object[]> object : listOfAllObjects){
            for (Object[] x : object){
                CustomerEnergyUsageForChart customerEnergyUsageForChart = new CustomerEnergyUsageForChart(x[0].toString(),x[1].toString());
                customerEnergyUsageForChartList.add(customerEnergyUsageForChart);
            }
        }
        model.addAttribute("customerEnergyUsageForChartList", customerEnergyUsageForChartList);
        return ResponseEntity.ok(customerEnergyUsageForChartList);
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
