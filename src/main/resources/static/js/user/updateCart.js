$(document).ready(function () {
    const products = $(".product");
    const btnUpdate = $(".update-box");

    btnUpdate.click(function (e) {
        e.preventDefault();

        let swal = null;
        Swal.fire({
            title: 'Are you sure?',
            text: "You won't be able to revert this!",
            icon: 'warning', // success, error, warning, info, question
            showCancelButton: true,
            confirmButtonColor: '#4bbe5a', // blue
            confirmButtonText: 'Yes, update it!',
        }).then(result => {
            if (result.isConfirmed) {
                const id = $(this).data('id');

                const products = $(".product");

                const data = [];
                products.each(function () {
                    const product = $(this);
                    const id = product.data("id");

                    const amount = product.find("#amount").val();

                    data.push({
                        productId: id,
                        quantity: amount
                    });
                });

                $.ajax({
                    url: `/cart/update`,
                    method: 'PUT',
                    contentType: 'application/json',
                    data: JSON.stringify({
                        products: data
                    }),
                    beforeSend: function () {
                        swal = Swal.fire({
                            title: 'Please wait...',
                            text: 'We are updating your cart',
                            didOpen: () => {
                                Swal.showLoading()
                            }
                        })
                    },
                    success: function (data) {
                        swal.close();
                        Swal.fire({
                            title: 'Updated!',
                            text: 'Your cart has been updated.',
                            icon: 'success',
                            confirmButtonColor: '#4bbe5a'
                        })

                        $(".total_all").text('$' + data);
                    },
                    error: function (err) {
                        swal.close();
                        Swal.fire({
                            title: 'Error!',
                            text: 'Something went wrong.',
                            icon: 'error',
                            confirmButtonColor: '#4bbe5a'
                        })
                    }
                })
            }
        })
    })
})