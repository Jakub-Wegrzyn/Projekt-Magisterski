function poprawHaslo(){
    var oldPassword = document.getElementById("oldPassword").value;
    var newPassword = document.getElementById("newPassword").value;
    console.log(oldPassword)
    console.log(newPassword)

    $.ajax({
        type: 'POST',
        url: '/customerdata/changepassword',
        data: {
            oldPassword: oldPassword,
            newPassword: newPassword
        },
        success: function (response) {
            alert("Hasło zostało zmienione")
        },
        error: function () {
            alert("Nie zmieniono hasła, podaj poprawne stare hasło!")
        },
    });
}