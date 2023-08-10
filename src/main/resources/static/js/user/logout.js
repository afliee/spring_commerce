$(document).ready(function () {
    const token = localStorage.getItem("token");
    if (!document.cookie.split(";").some((item) => item.trim().startsWith("user="))) {
        localStorage.removeItem("token");
        const sideLogout = $(".side-logout");
        sideLogout.remove();
        return;
    }
    if (!token) {
        return;
    }

    const btnLogout = $(".btn-logout");
    btnLogout.click(function (e) {
        e.preventDefault();

        let swal = null;
        $.ajax({
            url: `/api/auth/logout`,
            type: "POST",
            beforeSend: function () {
                swal = Swal.fire({
                    title: 'Please wait ...',
                    html: 'Logging out',
                    allowOutsideClick: false,
                    didOpen: () => {
                        Swal.showLoading()
                    }
                });
            },
            success: function (data) {
                localStorage.removeItem("token");
                if (document.cookie.split(";").some((item) => item.trim().startsWith("user="))) {
                    document.cookie = "user=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
                }
                swal.close();
                Swal.fire({
                    title: 'Success!',
                    text: 'You have been logged out!',
                    icon: 'success',
                    confirmButtonText: 'OK'
                }).then(result => {
                    if (result.isConfirmed) {
                        window.location.href = "/";
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
                }).then(result => {
                    if (result.isConfirmed) {
                        window.location.href = "/";
                    }
                })
            }
        })
    })
})