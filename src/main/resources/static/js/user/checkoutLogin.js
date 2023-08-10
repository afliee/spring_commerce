$(document).ready(function () {
    const formLogin = $("#formLogin");

    const btnLogin = $(".btn-login");

    btnLogin.click(function (e) {
        e.preventDefault();

        const username = $("#InputUsernameLogin").val();
        const password = $("#InputPasswordLogin").val();

        const data = {
            username: username,
            password: password,
            role: "USER"
        };

        let swal = null;

        $.ajax({
            url: `/api/auth/authenticate`,
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(data),
            beforeSend: function () {
                swal = Swal.fire({
                    title: 'Please wait ...',
                    html: 'Logging in',
                    text: 'You will be redirected to the homepage',
                    allowOutsideClick: false,
                    didOpen: () => {
                        Swal.showLoading()
                    }
                })
            },
            success: function (data) {
                localStorage.setItem("token", data.token);
                swal.close();
                Swal.fire({
                    title: 'Success!',
                    text: 'You have been logged in!',
                    icon: 'success',
                    confirmButtonText: 'OK'
                }).then(result => {
                    if (result.isConfirmed) {
                        window.location.href = "/cart";
                    }
                })
            },
            error: function (err) {
                console.log(err);
                swal.close();
                Swal.fire({
                    title: 'Error!',
                    text: 'Something went wrong!',
                    icon: "error",
                    confirmButtonText: 'OK'
                }).then(result => {
                    if (result.isConfirmed) {
                        window.location.href = "/login";
                    }
                })
            }
        })
    })
})