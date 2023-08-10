$(document).ready(function () {
    const formRegister = $("#formRegister");

    const btnRegister = $(".btn-register");
    btnRegister.click(function (e) {
        e.preventDefault();

        const username = $("#InputUsername").val();
        const password = $("#InputPassword").val();

        const data = {
            username: username,
            password: password,
            role: "USER"
        }
        let swal = null;
        Swal.fire({
            title: 'Are you sure?',
            text: "Check your information before submit!",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33'
        }).then(result => {
            if (result.isConfirmed) {
                $.ajax({
                    url: `/api/auth/signup`,
                    type: "POST",
                    contentType: "application/json",
                    data: JSON.stringify(data),
                    beforeSend: function () {
                        swal = Swal.fire({
                            title: 'Please wait ...',
                            html: 'Creating account',
                            allowOutsideClick: false,
                            didOpen: () => {
                                Swal.showLoading()
                            },
                        })
                    },
                    success: function (data) {
                        swal.close();
                        Swal.fire({
                            title: 'Success!',
                            text: 'Your account has been created!',
                            icon: 'success',
                            confirmButtonText: 'OK'
                        }).then(result => {
                            if (result.isConfirmed) {
                                window.location.href = "/login";
                            }
                        })
                    },
                    error: function (err) {
                        console.log(err.responseText);
                        swal.close();
                        Swal.fire({
                            title: 'Error!',
                            text: 'Something went wrong!',
                            icon: "error",
                            confirmButtonText: 'OK'
                        })
                    }
                })
            }
        })
    })
})