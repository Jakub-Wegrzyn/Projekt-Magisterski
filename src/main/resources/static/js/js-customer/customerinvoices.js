$.ajax({
    type: 'GET',
    url: '/customerinvoices/search',
    data: {},
    success: function (response) {
        var invoicesDtoList = response;
        for (var i = 0; i < invoicesDtoList.length; i++) {
            var invoicesDtoListElement = invoicesDtoList[i];
            $('#invoiceTable tbody').append(
                '<tr>' +
                '<td>' + invoicesDtoListElement.invoiceNumber + '</td>' +
                '<td data-amount="' + invoicesDtoListElement.ammount + '">' + invoicesDtoListElement.ammount + '</td>' +
                '<td>' + invoicesDtoListElement.billingPeriodFrom + '</td>' +
                '<td>' + invoicesDtoListElement.billingPeriodUntil + '</td>' +
                '<td>' + invoicesDtoListElement.issueDate + '</td>' +
                '<td>' + invoicesDtoListElement.issueLastDatePayment + '</td>' +
                '<td>' + invoicesDtoListElement.paymentStatus + '</td>' +
                '<td>' +
                '<button id="download"> <i class="download-icon">&#xf1c1;</i> Pobierz PDF </button>' +
                '</td>' +
                '<td>' +
                '<input type="checkbox" id="checkbox_' + invoicesDtoListElement.id + '" name="selectedItems"' + (invoicesDtoListElement.paymentStatus == "Opłacono" ? ' disabled' : '') + '>' +
                '</td>' +
                '</tr>'
            );
        }
    },
    error: function () {
        console.log('Nie udało się znaleźć klienta o ID:', customerId);
    }
});

$('#invoiceTable').on('click', '#download', function () {
    var row = $(this).closest('tr');
    var invoiceNumber = row.find('td:eq(0)').text();
    $.ajax({
        type: 'GET',
        url: '/customerinvoices/pobierz',
        data: {
            invoiceNumber: invoiceNumber
        },
        success: function (response) {
            var newUrl = '/customerinvoices/pobierz?invoiceNumber=' + invoiceNumber;
            window.location.href = newUrl;
        },
        error: function (error) {
            console.log("Nie pobrano")
        }
    });
});


let googlePayButtonAdded = false;
const google_pay_button = document.getElementById('google_pay_button');

function handleCheckboxClick() {
    const checkbox = document.getElementById('agreeToTermscheckbox');
    var checkboxes = document.querySelectorAll('input[name="selectedItems"]:checked');
    if (checkbox.checked && !googlePayButtonAdded && checkboxes.length > 0) {
        const paymentsClient = getGooglePaymentsClient();
        paymentsClient.isReadyToPay(getGoogleIsReadyToPayRequest())
            .then(function (response) {
                if (response.result) {
                    addGooglePayButton();
                }
            })
            .catch(function (err) {
                console.error(err);
            });
        googlePayButtonAdded = true;
    } else {
        google_pay_button.innerHTML = '';
        googlePayButtonAdded = false;
    }
}


const baseRequest = {
    apiVersion: 2,
    apiVersionMinor: 0
};
const allowedCardNetworks = ["MASTERCARD", "VISA"];

const allowedCardAuthMethods = ["PAN_ONLY", "CRYPTOGRAM_3DS"];

var paymentDataRequest;

const tokenizationSpecification = {
    type: 'PAYMENT_GATEWAY',
    parameters: {
        'gateway': 'example',
        'gatewayMerchantId': 'exampleGatewayMerchantId'
    }
};

const baseCardPaymentMethod = {
    type: 'CARD',
    parameters: {
        allowedAuthMethods: allowedCardAuthMethods,
        allowedCardNetworks: allowedCardNetworks
    }
};

const cardPaymentMethod = Object.assign(
    {},
    baseCardPaymentMethod,
    {
        tokenizationSpecification: tokenizationSpecification
    }
);

let paymentsClient = null;

function getGoogleIsReadyToPayRequest() {
    return Object.assign(
        {},
        baseRequest,
        {
            allowedPaymentMethods: [baseCardPaymentMethod]
        }
    );
}

function getGooglePaymentDataRequest() {
    paymentDataRequest = Object.assign({}, baseRequest);
    paymentDataRequest.allowedPaymentMethods = [cardPaymentMethod];
    paymentDataRequest.transactionInfo = getGoogleTransactionInfo();
    paymentDataRequest.merchantInfo = {
        merchantId: '22081998765477953379',
        merchantName: 'Energy Company'
    };

    return paymentDataRequest;
}

function getGooglePaymentsClient() {
    if (paymentsClient === null) {
        paymentsClient = new google.payments.api.PaymentsClient({environment: 'TEST'});
    }
    return paymentsClient;
}

function addGooglePayButton() {
    const paymentsClient = getGooglePaymentsClient();
    const button =
        paymentsClient.createButton({
            buttonColor: 'black',
            buttonType: 'pay',
            buttonLocale: 'pl',
            buttonSizeMode: 'fill',
            onClick: onGooglePaymentButtonClicked,
            allowedPaymentMethods: [baseCardPaymentMethod]
        });

    document.getElementById('google_pay_button').appendChild(button);

}

function getGoogleTransactionInfo() {
    var selectedAmounts = [];
    var checkboxes = document.querySelectorAll('input[name="selectedItems"]:checked');
    checkboxes.forEach(function (checkbox) {
        var row = checkbox.closest('tr');
        var amount = row.querySelector('td[data-amount]').getAttribute('data-amount');
        selectedAmounts.push(parseFloat(amount));
    });
    var sum = 0;
    for (var i = 0; i < selectedAmounts.length; i++) {
        sum += selectedAmounts[i];
    }
    return {
        countryCode: 'PL',
        currencyCode: 'PLN',
        totalPriceStatus: 'FINAL',
        totalPrice: `${sum.toFixed(2)}`
    };
}

function onGooglePaymentButtonClicked() {
    paymentDataRequest = getGooglePaymentDataRequest();
    paymentDataRequest.transactionInfo = getGoogleTransactionInfo();

    const paymentsClient = getGooglePaymentsClient();
    paymentsClient.loadPaymentData(paymentDataRequest)
        .then(function (paymentData) {
            processPayment(paymentData);

        })
        .catch(function (err) {
            console.error(err);
        });
}


function processPayment(paymentData) {
    var requestData = {googlePayData: paymentData.paymentMethodData.tokenizationData.token}
    const table = document.getElementById('invoiceTable');
    const rows = table.getElementsByTagName('tr');
    const selectedInvoiceNumbers = [];
    for (let i = 0; i < rows.length; i++) {
        const checkbox = rows[i].querySelector('input[type="checkbox"]');

        if (checkbox && checkbox.checked) {
            const invoiceNumberCell = rows[i].getElementsByTagName('td')[0];
            selectedInvoiceNumbers.push(invoiceNumberCell.textContent)
        }
    }
    $.ajax({
        type: 'POST',
        url: '/customerinvoices/platnosc',
        contentType: 'application/json',
        data: JSON.stringify({
            selectedInvoiceNumbers: selectedInvoiceNumbers
        }),
        success: function (response) {
            location.reload();
        },
        error: function (error) {
            console.log("Nie pobrano")
        }
    });
}

const googlePayScript = document.createElement('script');
googlePayScript.src = "https://pay.google.com/gp/p/js/pay.js";
googlePayScript.onload = onGooglePayLoaded;
document.body.appendChild(googlePayScript);
