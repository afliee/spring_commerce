$(document).ready(function () {
    const products = $(".product");

    products.each(function () {
        const product = $(this);
        const btnDelete = product.find(".remove-pr");

        btnDelete.click(function (e) {
            e.preventDefault();

            let swal = null;

            Swal.fire({
                title: 'Are you sure?',
                text: "You won't be able to revert this!",
                icon: 'warning', // success, error, warning, info, question
                showCancelButton: true,
                confirmButtonColor: '#4bbe5a', // blue
                confirmButtonText: 'Yes, delete it!',
            }).then(result => {
                if (result.isConfirmed) {
                    const id = $(this).data('id');

                    $.ajax({
                        url: `/cart/remove/${id}`,
                        method: 'DELETE',
                        beforeSend: function () {
                            swal = Swal.fire({
                                title: 'Please wait...',
                                text: 'We are deleting your product',
                                didOpen: () => {
                                    swal.showLoading()
                                }
                            })
                        },
                        success: function (data) {
                            swal.close();
                            product.remove();
                            Swal.fire({
                                title: 'Deleted!',
                                text: 'Your product has been deleted.',
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
})