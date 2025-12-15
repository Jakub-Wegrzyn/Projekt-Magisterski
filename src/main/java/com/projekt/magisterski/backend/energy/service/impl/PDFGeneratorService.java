package com.projekt.magisterski.backend.energy.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.projekt.magisterski.backend.energy.model.Customer;
import com.projekt.magisterski.backend.energy.model.CustomerEnergyUsage;
import com.projekt.magisterski.backend.energy.model.Invoices;
import com.projekt.magisterski.backend.energy.repo.CustomerRepository;
import com.projekt.magisterski.backend.energy.repo.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class PDFGeneratorService {

    private InvoiceRepository invoiceRepository;
    private CustomerRepository customerRepository;
    private CustomerEnergyUsage customerEnergyUsage;

    public PDFGeneratorService(InvoiceRepository invoiceRepository, CustomerRepository customerRepository, CustomerEnergyUsage customerEnergyUsage) {
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
        this.customerEnergyUsage = customerEnergyUsage;
    }

    private static void dodajNaglowek(Document document, LocalDate inputStartDate, LocalDate inputEndDate) throws DocumentException {
        Font fontTitle = FontFactory.getFont(FontFactory.TIMES);
        fontTitle.setSize(20);
        Font fontUnder = FontFactory.getFont(FontFactory.TIMES);
        fontTitle.setSize(15);
        Font fontCompany = FontFactory.getFont(FontFactory.TIMES);
        fontCompany.setSize(25);
        fontCompany.setStyle(Font.ITALIC);

        Paragraph paragraph = new Paragraph("Faktura za energie elektryczna", fontTitle);
        paragraph.setAlignment(Paragraph.ALIGN_LEFT);
        Paragraph paragraph2 = new Paragraph("obejmuje okres od " + inputStartDate + " do " + inputEndDate, fontUnder);
        paragraph2.setAlignment(Paragraph.ALIGN_LEFT);
        Paragraph paragraph3 = new Paragraph("Energy", fontCompany);
        paragraph3.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(paragraph);
        document.add(Chunk.NEWLINE);
        document.add(paragraph2);
        document.add(Chunk.NEWLINE);
        document.add(paragraph3);
    }

    private static void dodajTabele(Document document, Double a_plus, Double a_minus, Double ammount, LocalDate data_wystawienia, LocalDate terminPlatnosci, Customer customer) throws DocumentException {
        PdfPTable tabela = new PdfPTable(7);
        tabela.setWidthPercentage(100);
        tabela.setSpacingBefore(30f);
        tabela.setSpacingAfter(100f);

        dodajKomorka(tabela, "Imie", true);
        dodajKomorka(tabela, "Nazwisko", true);
        dodajKomorka(tabela, "A+", true);
        dodajKomorka(tabela, "A-", true);
        dodajKomorka(tabela, "Kwota", true);
        dodajKomorka(tabela, "Data wystawienia", true);
        dodajKomorka(tabela, "Termin płatności", true);

        dodajKomorka(tabela, customer.getFirstName(), false);
        dodajKomorka(tabela, customer.getLastName(), false);
        dodajKomorka(tabela, String.valueOf(a_plus), false);
        dodajKomorka(tabela, String.valueOf(a_minus), false);
        dodajKomorka(tabela, String.valueOf(ammount), false);
        dodajKomorka(tabela, String.valueOf(data_wystawienia), false);
        dodajKomorka(tabela, String.valueOf(terminPlatnosci), false);

        document.add(tabela);
    }

    private static void dodajKomorka(PdfPTable tabela, String tekst, boolean czyNaglowek) {
        PdfPCell komorka = new PdfPCell(new Phrase(tekst, new Font(Font.TIMES_ROMAN, 12, czyNaglowek ? Font.BOLD : Font.NORMAL)));
        komorka.setHorizontalAlignment(Element.ALIGN_CENTER);
        komorka.setVerticalAlignment(Element.ALIGN_MIDDLE);
        tabela.addCell(komorka);
    }

    private static void dodajNaglowek(Document document) throws DocumentException {
        Font fontTitle = FontFactory.getFont(FontFactory.TIMES);
        fontTitle.setSize(10);

        Paragraph paragraph = new Paragraph("Dziękujemy za skorzystanie z usług firmy Energy 2024", fontTitle);
        paragraph.setAlignment(Paragraph.ALIGN_LEFT);

        document.add(paragraph);
        document.add(Chunk.NEWLINE);

    }

    public void generateAndSaveInvoice(String startDate, String endDate, String customerId) {

        Optional<Customer> optionalCustomer = customerRepository.findById(Long.valueOf(customerId));

        if (optionalCustomer.isPresent()) {
            Customer customer = new Customer();
            customer = optionalCustomer.get();
            String numerPPE = customer.getPPE();
            System.out.println("numerPPE " + numerPPE);
            String tableName = "energyusage_" + numerPPE;
            String inputStartDate = generateDateTime(startDate);
            String inputEndDate = generateDateTime(endDate);
            System.out.println("inputStartDate " + inputStartDate);
            List<Object[]> dataList = customerEnergyUsage.getDataFromTableWithTimeRange(tableName, inputStartDate, inputEndDate);

            if (!dataList.isEmpty()) {
                Double suma_a_plus = 0.0;
                Double suma_a_minus = 0.0;
                Double kwota = 0.0;
                Double cena_za_a_plus = 0.8;
                Double cena_za_a_minus = 0.3;
                for (Object[] row : dataList) {
                    suma_a_plus = (Double) row[0];
                    suma_a_minus = (Double) row[1];
                }
                Double kwota_do_dodania = suma_a_plus * cena_za_a_plus;
                Double kwota_do_odjecia = suma_a_minus * cena_za_a_minus;
                kwota = kwota_do_dodania - kwota_do_odjecia;
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                String kwota_rounded = decimalFormat.format(kwota);
                double kwota_koncowa = Double.parseDouble(kwota_rounded.replace(",", "."));
                LocalDate currentDate = LocalDate.now();
                LocalDate termin_platnosci = currentDate.plusWeeks(4);
                LocalDateTime currentDateTime = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDateTime = currentDateTime.format(formatter);
                String firstName = bezPolskichZnakow(customer.getFirstName());
                String lastName = bezPolskichZnakow(customer.getLastName());
                String invoiceNumber = "INV-" + formattedDateTime + "-" + firstName + "-" + lastName;

                byte[] pdfData = generateInvoice(suma_a_plus, suma_a_minus, kwota_koncowa,
                        toLocalDate(inputStartDate), toLocalDate(inputEndDate), LocalDate.now(), termin_platnosci, customer);
                Invoices invoice = new Invoices();
                invoice.setPdfData(pdfData);
                invoice.setAmmount(kwota_koncowa);
                invoice.setInvoiceNumber(invoiceNumber);
                invoice.setIssueDate(LocalDate.now());
                invoice.setBillingPeriodFrom(toLocalDate(inputStartDate));
                invoice.setBillingPeriodUntil(toLocalDate(inputEndDate));
                invoice.setIssueLastDatePayment(termin_platnosci);
                invoice.setCustomer(customer);
                invoice.setPaymentStatus("Oczekujący");
                invoiceRepository.save(invoice);
            }
        } else {
            System.out.println("Klient o podanym identyfikatorze nie istnieje.");
        }
    }

    public String bezPolskichZnakow(String customer) {
        String cleanedName = Normalizer.normalize(customer, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .toLowerCase();
        return cleanedName;
    }

    public byte[] pobierzFakture(String invoiceNumber) {

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/energy_database?useSSL=false&allowPublicKeyRetrieval=true", "root", "root")) {
            String sql = "SELECT plik_pdf FROM invoices WHERE numer_faktury = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, invoiceNumber);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        byte[] pdfData = resultSet.getBytes("plik_pdf");
                        return pdfData;
                    } else {
                        System.out.println("Nie znaleziono faktury o podanym numerze.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private LocalDate toLocalDate(String data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(data, formatter);
        LocalDate localDate = localDateTime.toLocalDate();
        return localDate;
    }

    private String generateDateTime(String data) {
        LocalDate inputStartDate = LocalDate.parse(data);
        LocalDateTime dateTime = inputStartDate.atStartOfDay();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = dateTime.format(formatter);
        return formattedDateTime;
    }

    private byte[] generateInvoice(Double a_plus, Double a_minus, Double ammount, LocalDate inputStartDate, LocalDate inputEndDate, LocalDate data_wystawienia, LocalDate terminPlatnosci, Customer customer) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);

            document.open();
            dodajNaglowek(document, inputStartDate, inputEndDate);
            dodajTabele(document, a_plus, a_minus, ammount, data_wystawienia, terminPlatnosci, customer);
            dodajNaglowek(document);
            document.close();

            return baos.toByteArray();

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Błąd podczas generowania faktury PDF", e);
        }
    }


}
