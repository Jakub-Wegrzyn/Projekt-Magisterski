$(document).ready(function () {

    $('#submitBtn').click(function () {
        var startDate = $('#startDate').val();
        var endDate = $('#endDate').val();
        var ammountofa_plus = document.getElementById("ammountofa_plus");
        var ammountofa_minus = document.getElementById("ammountofa_minus");
        if (startDate && endDate) {
            var startDateObj = new Date(startDate);
            var endDateObj = new Date(endDate);
            if (startDateObj >= endDateObj) {
                alert('Data odczytu od jest większa od daty odczytu do');
            } else {
                var oneDayInMillis = 24 * 60 * 60 * 1000;
                if ((endDateObj - startDateObj) < oneDayInMillis) {
                    alert('Wybierz przynajmniej jeden dzień');
                } else {
                    $('#loadingIndicator').show();
                    $.ajax({
                        type: 'POST',
                        url: '/counterdata/search',
                        data: {
                            startDate: startDate,
                            endDate: endDate
                        },
                        success: function (response) {
                            $('#energyUsageTable').css('display', 'table');
                            $('#energyUsageTable tbody').empty();

                            var energyUsageList = response;
                            for (var i = 0; i < energyUsageList.length; i++) {
                                var energyUsage = energyUsageList[i];
                                $('#energyUsageTable tbody').append('<tr><td>' + energyUsage.formattedDate + '</td><td>' + energyUsage.aplus + '</td><td>' + energyUsage.aminus + '</td></tr>');
                            }
                        },
                        error: function () {
                            console.log('Nie udało się pobrać danych:');
                            $('#energyUsageTable tbody').empty();
                            alert('Wystąpił błąd: ' + error.message);
                        },
                        complete: function () {
                            $('#loadingIndicator').hide();
                        }
                    });

                    $.ajax({
                        type: 'POST',
                        url: '/counterdata/searchammount',
                        data: {
                            startDate: startDate,
                            endDate: endDate
                        },
                        success: function (response) {
                            var sumEnergyUsageList = response;
                            ammountofa_plus.value = sumEnergyUsageList[0].suma_a_plus;
                            ammountofa_minus.value = sumEnergyUsageList[0].suma_a_minus;
                        },
                        error: function () {
                            console.log('Nie udało się pobrać danych:');

                        },
                    });
                }

            }
        }


    });
});