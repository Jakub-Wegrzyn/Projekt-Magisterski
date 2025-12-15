package com.projekt.magisterski.backend.energy.web;

import com.projekt.magisterski.backend.energy.model.Customer;
import com.projekt.magisterski.backend.energy.model.CustomerEnergyUsage;
import com.projekt.magisterski.backend.energy.repo.CustomerRepository;
import com.projekt.magisterski.backend.energy.repo.InvoiceRepository;
import com.projekt.magisterski.backend.energy.service.impl.PDFGeneratorService;
import com.projekt.magisterski.backend.energy.web.dto.InvoicesDto;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/customerinvoices")
public class CustomerInvoicesController {

    private CustomerRepository customerRepository;
    private CustomerEnergyUsage customerEnergyUsage;
    private PDFGeneratorService pdfGeneratorService;
    private InvoiceRepository invoiceRepository;

    public CustomerInvoicesController(CustomerRepository customerRepository, CustomerEnergyUsage customerEnergyUsage, PDFGeneratorService pdfGeneratorService, InvoiceRepository invoiceRepository) {
        this.customerRepository = customerRepository;
        this.customerEnergyUsage = customerEnergyUsage;
        this.pdfGeneratorService = pdfGeneratorService;
        this.invoiceRepository = invoiceRepository;
    }

    @GetMapping
    public String customerInvoices(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Customer customer = customerRepository.findByEmail(email);
        model.addAttribute(customer);
        return "customerinvoices";
    }

    @GetMapping("/pobierz")
    public ResponseEntity<byte[]> pobierzFakture(@RequestParam(name = "invoiceNumber", required = false) String invoiceNumber, HttpServletResponse response) {
        byte[] pdfData = pdfGeneratorService.pobierzFakture(invoiceNumber);
        String headerKey = "Content-Disposition";
        String headerValue = "atachment; filename=" + invoiceNumber + ".pdf";
        response.setHeader(headerKey, headerValue);

        return new ResponseEntity<>(pdfData, HttpStatus.OK);
    }


    @GetMapping("/search")
    public ResponseEntity<List<InvoicesDto>> customerInvoicesSearch(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Customer customer = customerRepository.findByEmail(email);
        List<InvoicesDto> customerInvoicesDtoList = new ArrayList<>();
        List<Object[]> dataList = customerEnergyUsage.getInvoicesByCurrentUser(customer.getId().toString());
        if (!dataList.isEmpty()) {
            for (Object[] row : dataList) {
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

                InvoicesDto invoicesDto = new InvoicesDto(numerFaktury, dataWystawienia, okresRozliczeniowyOd, okresRozliczeniowyDo, terminPlatnosci,
                        customer, kwota, statusPlatnosci, nazwaPdf, plikPdf);
                customerInvoicesDtoList.add(invoicesDto);
            }
            model.addAttribute("customerInvoicesDtoList", customerInvoicesDtoList);
            return ResponseEntity.ok(customerInvoicesDtoList);
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/platnosc")
    @Transactional
    public String pobierzFakture(@RequestBody InvoiceRequest invoiceRequest) {
        List<String> listaNumerowFaktur = invoiceRequest.getSelectedInvoiceNumbers();
        listaNumerowFaktur.forEach(invoiceNumber -> invoiceRepository.updatePaymentStatus(invoiceNumber, "Opłacono"));
        return "customerinvoices";
    }

    public static class InvoiceRequest {
        private List<String> selectedInvoiceNumbers;

        public List<String> getSelectedInvoiceNumbers() {
            return selectedInvoiceNumbers;
        }

        public void setSelectedInvoiceNumbers(List<String> selectedInvoiceNumbers) {
            this.selectedInvoiceNumbers = selectedInvoiceNumbers;
        }
    }
}
