$(document).ready(function () {
    const btnPlace = $(".btn-place");

    btnPlace.click(function (e) {
        e.preventDefault();
        if (!document.cookie.split(';').filter((item) => item.includes('user=')).length) {
            Swal.fire({
                icon: 'error',
                title: 'Oops...',
                text: 'You must be logged in to place an order!',
                showConfirmButton: true,
                showCancelButton: false,
                confirmButtonText: 'Login'
            }).then(result => {
                if (result.isConfirmed) {
                    window.location.href = "/login";
                }
            })
        }

        const firstName = $("#firstName").val();
        const lastName = $("#lastName").val();
        const username = $("#username").val();
        const email = $("#email").val();
        const address = $("#address").val();
        const address2 = $("#address2").val();
        const phone = $("#phone").val();

        const totalAll = $(".total_all").text().replace("$", "").trim();

        const data = {
            firstName: firstName,
            lastName: lastName,
            username: username,
            email: email,
            address: address,
            address2: address2,
            total: totalAll,
            phone: phone
        }

        let swal = null;

        Swal.fire({
            title: 'Are you sure?',
            text: "You won't be able to revert this!",
            icon: 'warning',
            showCancelButton: true,
            showConfirmButton: true,
            confirmButtonText: 'Yes, place it!',
            cancelButtonText: 'No, cancel!',
        }).then(result => {
            if (result.isConfirmed) {
                $.ajax({
                    url: "/order/create",
                    type: "POST",
                    contentType: "application/json",
                    data: JSON.stringify(data),
                    beforeSend: function() {
                        swal = Swal.fire({
                            title: 'Please wait...',
                            didOpen: () => {
                                Swal.showLoading()
                            }
                        })
                    },
                    success: function (response) {
                        swal.close();
                        Swal.fire({
                            icon: 'success',
                            title: 'Success!',
                            text: 'Your order has been placed!',
                            showConfirmButton: true,
                            showCancelButton: false,
                        }).then(result => {
                            if (result.isConfirmed) {
                                window.location.href = "/";
                            }
                        })
                    },
                    error: function (response) {
                        console.log(response);
                        swal.close();
                        Swal.fire({
                            icon: 'error',
                            title: 'Oops...',
                            text: 'Something went wrong!',
                            showConfirmButton: true,
                        })
                    }
                })
            }
        })
    })
})