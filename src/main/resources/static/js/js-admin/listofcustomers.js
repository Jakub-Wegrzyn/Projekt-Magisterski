$(document).ready(function () {
    $('.btn-usun').click(function () {
        var row = $(this).closest("tr");
        var customerId = row.find("td:first-child").text();

        $.ajax({
            type: 'POST',
            url: '/listofcustomers',
            data: {
                customerId: customerId,
                action: 'usun'
            },
            success: function (response) {
                location.reload()
            },
            error: function () {
            }
        });
    });
    $('.btn-primary').click(function(){
        var row = $(this).closest("tr");
        var customerId = row.find("td:first-child").text();
        $.ajax({
            type: 'POST',
            url: '/listofcustomers',
            data: {
                customerId: customerId,
                action: 'update'
            },
            success: function (response) {
                window.location.href = response;
            },
            error: function () {
            }
        });
    });
});