$(document).ready(function () {
    $('#submitBtn').click(function () {
        var startDate = $('#startDate').val();
        var endDate = $('#endDate').val();

        var ammountofa_plus = document.getElementById("ammountofa_plus");
        var ammountofa_minus = document.getElementById("ammountofa_minus");

        var list_of_charts = ['wykres_zuzycie_a_plus', 'wykres_oddanie_a_minus']

        list_of_charts.forEach(function (element){
            var canvas = document.getElementById(element);
            var ctx = canvas.getContext('2d');
            var existingChart = Chart.getChart(ctx);
            if (existingChart) {
                existingChart.destroy();
            }
        })



        if (startDate && endDate) {
            var startDateObj = new Date(startDate);
            var endDateObj = new Date(endDate);
            if(startDateObj>= endDateObj){
                alert('Data odczytu od jest większa od daty odczytu do');
            }
            else{
                var oneDayInMillis = 24 * 60 * 60 * 1000; // liczba milisekund w jednym dniu
                if ((endDateObj - startDateObj) < oneDayInMillis) {
                    alert('Wybierz przynajmniej jeden dzień');
                }
                else{
                    $.ajax({
                        type: 'POST',
                        url: '/diagrams/search',
                        data: {
                            startDate: startDate,
                            endDate: endDate
                        },
                        success: function (response) {
                            var lista_a_plus = [];
                            var lista_a_minus = [];
                            response.forEach(function (item){
                                lista_a_plus.push(item.sum_a_plus);
                                lista_a_minus.push(item.sum_a_minus)
                            });
                            console.log(lista_a_minus)
                            wykres(lista_a_plus, lista_a_minus)
                        },
                        error: function () {
                            console.log('Nie udało się pobrać danych:');
                            alert('Wystąpił błąd: ' + error.message);
                        },
                        complete: function () {
                        }
                    });
                }

            }
        }
        $.ajax({
            type: 'POST',
            url: '/diagrams/searchammount',
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
    });
    function wykres(lista_a_plus, lista_a_minus){
        var zuzycie_a_plus = {
            labels: ['00:00 - 6:00', '6:00  - 12:00', '12:00 - 18:00', '18:00 - 24:00'],
            datasets: [{
                data: [lista_a_plus[0], lista_a_plus[1], lista_a_plus[2], lista_a_plus[3]],
                backgroundColor: [
                    'darkred',
                    'darkblue',
                    'yellow',
                    'darkgreen',
                ],
                borderColor: [
                    'darkred',
                    'darkblue',
                    'yellow',
                    'darkgreen',
                ],
                borderWidth: 1
            }]
        };

        var konfiguracja_a_plus_chart = {
            type: 'doughnut',
            data: zuzycie_a_plus,
            options: {
                responsive: true,
                maintainAspectRatio: false,
                legend: {
                    position: 'bottom'
                },
                plugins: {
                    title: {
                        display: true,
                        color: 'black',
                        text: 'Zużycie energii w kWh (A+)',
                        font: {
                            size: 16
                        }
                    }
                }
            }
        };
        var zuzycie_a_plus_crt = document.getElementById('wykres_zuzycie_a_plus').getContext('2d');
        var wykres_zuzycie_a_plus = new Chart(zuzycie_a_plus_crt, konfiguracja_a_plus_chart);

        var czyJedenWiekszyOdZera = lista_a_minus.some(function (element) {
            var liczba = parseFloat(element);
            return !isNaN(liczba) && liczba > 0;
        });

        if(czyJedenWiekszyOdZera){
            var zuzycie_a_minus = {
                labels: ['00:00 - 6:00', '6:00  - 12:00', '12:00 - 18:00', '18:00 - 24:00'],
                datasets: [{
                    data: [lista_a_minus[0], lista_a_minus[1], lista_a_minus[2], lista_a_minus[3]],
                    backgroundColor: [
                        'darkred',
                        'darkblue',
                        'yellow',
                        'darkgreen',
                    ],
                    borderColor: [
                        'darkred',
                        'darkblue',
                        'yellow',
                        'darkgreen',
                    ],
                    borderWidth: 1
                }]
            };

            var konfiguracja_a_minus_chart = {
                type: 'doughnut',
                data: zuzycie_a_minus,
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    legend: {
                        position: 'bottom'
                    },
                    plugins: {
                        title: {
                            display: true,
                            color: 'black',
                            text: 'Ilość oddanej energii w kWh (A-)',
                            font: {
                                size: 16
                            }
                        }
                    }
                }
            };
            var zuzycie_a_minus_crt = document.getElementById('wykres_oddanie_a_minus').getContext('2d');
            var wykres_zuzycie_a_minus = new Chart(zuzycie_a_minus_crt, konfiguracja_a_minus_chart);
        }
    }

});