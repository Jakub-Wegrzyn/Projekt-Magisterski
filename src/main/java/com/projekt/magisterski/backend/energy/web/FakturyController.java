package com.projekt.magisterski.backend.energy.web;

import com.projekt.magisterski.backend.energy.web.dto.InvoicesDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import com.projekt.magisterski.backend.energy.model.Customer;
import com.projekt.magisterski.backend.energy.model.CustomerEnergyUsage;
import com.projekt.magisterski.backend.energy.repo.CustomerRepository;
import com.projekt.magisterski.backend.energy.service.impl.PDFGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/faktury")
public class FakturyController {

    @Autowired
    private CustomerRepository customerRepository;
    private CustomerEnergyUsage customerEnergyUsage;
    private PDFGeneratorService pdfGeneratorService;

    public FakturyController(CustomerRepository customerRepository, CustomerEnergyUsage customerEnergyUsage, PDFGeneratorService pdfGeneratorService) {
        this.customerRepository = customerRepository;
        this.customerEnergyUsage = customerEnergyUsage;
        this.pdfGeneratorService = pdfGeneratorService;
    }

    @GetMapping
    public String faktury(String invoiceNumber, Model model)
    {
        List<Customer> customers = customerRepository.findAll();
        Map<Long, String> customerMap = new HashMap<>();
        for (Customer customer : customers) {
            String fullName = customer.getFirstName() + " " + customer.getLastName();
            customerMap.put(customer.getId(), fullName);
        }
        model.addAttribute("customerMap", customerMap);
        return "faktury";
    }

    @GetMapping("/pdf")
    public void wystawFakture(
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate,
            @RequestParam(name = "customerId", required = false) String customerId,
            HttpServletResponse response) {
        pdfGeneratorService.generateAndSaveInvoice(startDate, endDate, customerId);
    }

    @GetMapping("/pobierz")
    public ResponseEntity<byte[]> pobierzFakture(@RequestParam(name = "invoiceNumber", required = false) String invoiceNumber, HttpServletResponse response) {
        byte[] pdfData = pdfGeneratorService.pobierzFakture(invoiceNumber);
        String headerKey = "Content-Disposition";
        String headerValue = "atachment; filename=" + invoiceNumber + ".pdf";
        response.setHeader(headerKey, headerValue);

        return new ResponseEntity<>(pdfData, HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<List<InvoicesDto>> searchCustomerById(@RequestParam Long customerId, Model model) {
        List<InvoicesDto> invoicesDtoList = new ArrayList<>();
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            String tableName = "invoices";

            List<Object[]> dataList = customerEnergyUsage.getInvoicesByCurrentUser(customer.getId().toString());
            if (!dataList.isEmpty()) {
                for(Object[] row : dataList){
                    Long id = (Long) row[0];
                    Double kwota = (Double) row[1];
                    Date okresRozliczeniowyOdSql = (Date) row[2];
                    LocalDate okresRozliczeniowyOd = okresRozliczeniowyOdSql.toLocalDate();
                    Date okresRozliczeniowyDoSql = (Date) row[3];
                    LocalDate okresRozliczeniowyDo = okresRozliczeniowyDoSql.toLocalDate();
                    String numerFaktury = (String) row[4];
                    Date dataWystawieniaSql = (Date) row[5];
                    LocalDate dataWystawienia = dataWystawieniaSql.toLocalDate();
                    Date terminPlatnosciSql = (Date) row[6];
                    LocalDate terminPlatnosci = terminPlatnosciSql.toLocalDate();
                    String statusPlatnosci = (String) row[7];
                    byte[] plikPdf = (byte[]) row[8];
                    String nazwaPdf = (String) row[9];
                    Long userId = (Long) row[10];

                    InvoicesDto invoicesDto = new InvoicesDto(numerFaktury,dataWystawienia, okresRozliczeniowyOd, okresRozliczeniowyDo, terminPlatnosci,
                            customer,kwota,statusPlatnosci,nazwaPdf, plikPdf);
                    invoicesDtoList.add(invoicesDto);
                }
                model.addAttribute("invoicesDtoList", invoicesDtoList);
                return ResponseEntity.ok(invoicesDtoList);
            }
        } else {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.notFound().build();
    }
}
