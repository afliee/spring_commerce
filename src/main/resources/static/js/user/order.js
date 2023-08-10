$(document).ready(function () {
    const quantityBox = $(".quantity-box");
    const btnReverse = $(".btn-reverse");
    const btnPlaced = $(".btn-place");


    quantityBox.each(function (e) {
        const amount = $(this).find("#amount");
        console.log(amount)
        const price = amount.data("price");

        amount.on("change", function () {
            const value = $(this).val();
            console.log(value)
            const total = value * price;
            $(this).closest('.media').find(".quantity").text('Qty: ' + value);

            $(this).closest('.media').find(".subtotal").text(parseFloat(total).toFixed(1));

            const subtotals = $(".subtotal");
            let totalAmount = 0;
            subtotals.each(function (e) {
                totalAmount += parseFloat($(this).text());
            });
            $(".total_all").text(parseFloat(totalAmount).toFixed(1));
        })
    })

    btnReverse.click(function (e) {
        e.preventDefault();
        const id = $(this).data("id");

        let swal = null;

        Swal.fire({
            title: 'Are you sure?',
            text: "You want to delete this item from your cart?",
            icon: 'warning',
            showCancelButton: true,
            cancelButtonColor: '#3085d6',
            confirmButtonText: 'Yes, delete it!',
            cancelButtonText: 'No, keep it'
        }).then(result => {
            if (result.isConfirmed) {
                $.ajax({
                    url: `/order/reverse/${id}`,
                    type: "DELETE",
                    beforeSend: function () {
                        swal = Swal.fire({
                            title: 'Please wait ...',
                            allowOutsideClick: false,
                            showConfirmButton: false,
                            willOpen: () => {
                                Swal.showLoading()
                            }
                        })
                    },
                    success: function (data) {
                        swal.close();
                        Swal.fire({
                            title: 'Success!',
                            text: 'Item has been deleted from your cart.',
                            icon: 'success',
                            showConfirmButton: false,
                            timer: 1500
                        }).then(() => {
                            window.location.href = "/orders";
                        })
                    },
                    error: function (e) {
                        swal.close();
                        Swal.fire({
                            title: 'Error!',
                            text: 'Something went wrong.',
                            icon: 'error',
                            showConfirmButton: false,
                            timer: 1500
                        }).then(() => {
                            window.location.href = "/orders";
                        })
                    }
                })
            }
        })
    })


    btnPlaced.click(function (e) {
        e.preventDefault();
        const id = $(this).data("id");
        const orderDetails = $(".order-detail");
        let orderQuantity = '';
        orderDetails.each(function (e) {
            const id = $(this).data("order-id");
            const amount = $(this).find("#amount").val();
            orderQuantity += `${id}=${amount}&`;
        })
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
            phone: phone,
            cartQuantity: orderQuantity
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
                    url: `/order/update/${id}`,
                    type: "PUT",
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