function deleteProduct(
    {
        token = "",
        productId = "",
    }
) {
    if (!token || !productId) {
        Swal.fire({
            title: 'Error!',
            text: 'Something went wrong.',
            icon: 'error', // question, warning, error
            confirmButtonColor: '#64d052', // blue
            confirmButtonText: 'OK',
        })
        return;
    }
    let swal = null;

    Swal.fire({
        title: 'Are you sure?',
        text: "You won't be able to revert this!",
        icon: 'warning', // question, warning, error
        showCancelButton: true,
        confirmButtonColor: '#64d052', // blue
        confirmButtonText: 'Yes, delete it!',
    }).then(result => {
        if (result.isConfirmed) {
            $.ajax({
                url: `/product/delete/${productId}`,
                method: "DELETE",
                headers: {
                    'Authorization': `Bearer ${token}`
                },
                beforeSend: function () {
                    swal = Swal.fire({
                        title: 'Please wait ...',
                        html: 'Deleting product',
                        didOpen: () => {
                            Swal.showLoading()
                        }
                    })
                },
                success: function (data) {
                    swal.close();
                    Swal.fire({
                        title: 'Deleted!',
                        text: 'Your product has been deleted.',
                        icon: 'success', // question, warning, error
                        confirmButtonColor: '#64d052', // blue
                        confirmButtonText: 'OK',
                        timer: 2000
                    }).then(result => {
                        if (result.isConfirmed) {
                            window.location.reload();
                        }
                    })

                    setTimeout(function () {
                        window.location.reload();
                    }, 2000)
                },
                error: function (err) {
                    swal.close();
                    Swal.fire({
                        title: 'Error!',
                        text: 'Something went wrong.',
                        icon: 'error', // question, warning, error
                        confirmButtonColor: '#64d052', // blue
                        confirmButtonText: 'OK',
                        timer: 2000
                    })

                }
            })
        }
    })
}