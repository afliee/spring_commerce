$(document).ready(function () {
    const btnLogin = $(".btn-sign");
    const alert = $(".alert");
    btnLogin.click(function (e) {
        e.preventDefault();
        const username = $("#username").val();
        const password = $("#password").val();
        const role = $("#role").val();

        const data = {username, password, role};

        $.ajax({
            url: `/api/auth/authenticate`,
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(data),
            success: function (response) {
                console.log(response);
                localStorage.setItem("token", response.token);
                localStorage.setItem("username", response.username);
                window.location.href = "/admin/dashboard";
            },
            error: function (error) {
                console.log(error.responseJSON.message);
                alert.text(error.responseJSON.message);
            }
        })

    })
})