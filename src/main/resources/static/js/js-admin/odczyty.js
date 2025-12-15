$(document).ready(function () {
    document.getElementById('txtSearch').value = '';
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

    $('#searchBtn').click(function () {

        $('#loadingIndicator').show();
        var selectedSuggestion = $('#txtSearch').val();
        var customerId = getKeyByValue(customerMap, selectedSuggestion);

        $.ajax({
            type: 'POST',
            url: '/odczyty/search',
            data: {customerId: customerId},
            success: function (response) {

                $('#energyUsageTable').css('display', 'table');
                $('#energyUsageTable tbody').empty();

                var energyUsageList = response;
                for (var i = 0; i < energyUsageList.length; i++) {
                    var energyUsage = energyUsageList[i];
                    $('#energyUsageTable tbody').append(
                        '<tr>' +
                        '<td>' + energyUsage.id + '</td>' +
                        '<td>' + energyUsage.formattedDate + '</td>' +
                        '<td>' + energyUsage.aplus + '</td>' +
                        '<td>' + energyUsage.aminus + '</td>' +
                        '<td><button type="button" class="btn btn-primary">Edytuj</button></td> ' +
                        '</tr>');
                }
            },
            error: function () {
                console.log('Nie udało się znaleźć klienta o ID:', customerId);
            },
            complete: function () {
                $('#loadingIndicator').hide();
            }
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
                console.log(customerId)
                $('#txtSearch').val(selectedSuggestion);
                $('#txtSearch').data('customer-id', customerId);
                suggestionsContainer.css('display', 'none');
            });
        } else if ($('#txtSearch').val().length > 0) {
            suggestionsContainer.append('<div class="no-suggestions">Brak sugestii</div>');
        } else {
            suggestionsContainer.css('display', 'none');
        }
    }

    function getKeyByValue(object, value) {
        return Object.keys(object).find(key => object[key] === value);
    }

    $('#energyUsageTable').on('click', 'button.btn-primary', function() {
        var row = $(this).closest('tr');
        var customerId = $('#txtSearch').data('customer-id');
        var singleMeasureId = row.find("td:eq(0)").text();

        $.ajax({
            type: 'POST',
            url: '/odczyty/correctmeasure',
            data: {
                customerId: customerId,
                singleMeasureId: singleMeasureId
            },
            success: function (response) {
                console.log(response)
                window.location.href = response
            },
            error: function () {
            }
        });

    });
});