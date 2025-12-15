$(document).ready(function () {
    var suggestionsContainer = $('#suggestions');

    $('#txtSearch').on('input', function () {
        var value = $(this).val();

        if (value.trim() === '') {
            suggestionsContainer.css('display', 'none');
        } else {
            displaySuggestions(filterSuggestions(value));
        }
    });

    $('#expandBtn').click(function () {
        displaySuggestions(Object.values(customerMap));
    });

    $('#wystawBtn').click(function () {

        var selectedSuggestion = $('#txtSearch').val();
        var customerId = getKeyByValue(customerMap, selectedSuggestion);
        var startDate = $('#startDate').val();
        var endDate = $('#endDate').val();
        var generatePdf = "generatePdf";

        console.log(startDate)
        console.log(endDate)
        console.log(customerId)

        $.ajax({
            type: 'GET',
            url: '/faktury/pdf',
            data: {
                startDate: startDate,
                endDate: endDate,
                customerId: customerId,
                generatePdf: generatePdf
            },
            success: function (response) {
                getTableData(customerId);
            },
        });
    });

    function filterSuggestions(keyword) {
        return Object.values(customerMap).filter(function (fullName) {
            return fullName.toLowerCase().includes(keyword.toLowerCase());
        });
    }

    function displaySuggestions(suggestions) {
        var suggestionsContainer = $('#suggestions');
        suggestionsContainer.empty();

        if (suggestions.length > 0) {
            suggestionsContainer.css('display', 'block');
            for (var i = 0; i < suggestions.length; i++) {
                var suggestion = suggestions[i];
                var customerId = getKeyByValue(customerMap, suggestion);
                suggestionsContainer.append('<div class="suggestion" data-customer-id="' + customerId + '">' + suggestion + '</div>');
            }

            $('.suggestion').click(function () {
                var selectedSuggestion = $(this).text();
                var customerId = $(this).data('customer-id');
                $('#txtSearch').val(selectedSuggestion);
                var searchText = $('#txtSearch').val().toLowerCase();
                getTableData(customerId)
                suggestionsContainer.css('display', 'none');
            });
        } else if ($('#txtSearch').val().length > 0) {
            suggestionsContainer.append('<div class="no-suggestions">Brak sugestii</div>');
        } else {
            suggestionsContainer.css('display', 'none');
        }
    }

    $('#invoiceTable').on('click', '#download', function () {
        var row = $(this).closest('tr');
        var invoiceNumber = row.find('td:eq(0)').text();
        $.ajax({
            type: 'GET',
            url: '/faktury/pobierz',
            data: {
                invoiceNumber: invoiceNumber
            },
            success: function (response) {
                var newUrl = '/faktury/pobierz?invoiceNumber=' + invoiceNumber;
                window.location.href = newUrl;
            },
            error: function (error) {
                console.log("Nie pobrano")
            }
        });
    });

    function getTableData(customerId) {
        $('#invoiceTable tbody').empty();
        $.ajax({
            type: 'POST',
            url: '/faktury/search',
            data: {customerId: customerId},
            success: function (response) {
                console.log('Znaleziony klient:', response);
                var invoicesDtoList = response;
                for (var i = 0; i < invoicesDtoList.length; i++) {
                    var invoicesDtoListElement = invoicesDtoList[i];
                    $('#invoiceTable tbody').append(
                        '<tr>' +
                        '<td>' + invoicesDtoListElement.invoiceNumber + '</td>' +
                        '<td>' + invoicesDtoListElement.ammount + '</td>' +
                        '<td>' + invoicesDtoListElement.billingPeriodFrom + '</td>' +
                        '<td>' + invoicesDtoListElement.billingPeriodUntil + '</td>' +
                        '<td>' + invoicesDtoListElement.issueDate + '</td>' +
                        '<td>' + invoicesDtoListElement.issueLastDatePayment + '</td>' +
                        '<td>' + invoicesDtoListElement.paymentStatus + '</td>' +
                        '<td class="d-flex flex-column">' +
                        '<button id="download" class="btn btn-primary btn-sm mb-2">Pobierz</button>' +
                        '</td>' +
                        '</tr>'
                    );
                }
            },
            error: function () {
                console.log('Nie udało się znaleźć klienta o ID:', customerId);
            },
            complete: function () {
                $('#loadingIndicator').hide();
            }
        });
    }

    function getKeyByValue(object, value) {
        return Object.keys(object).find(key => object[key] === value);
    }
});