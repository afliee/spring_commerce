$(document).ready(function () {
    const TOKEN = localStorage.getItem("token");

    const btnUpdate = $(".btn-update");

    const id = $("#id").val();
    const categoryId = $("#categoryId").val();

    const name = $("#name");
    const price = $("#price");
    const description = $("#description");
    const image = $("#image");
    const quantity = $("#quantity");

    btnUpdate.on('click', function (e) {
        e.preventDefault();
        let swal = null;
        Swal.fire({
            title: 'Are you sure?',
            text: "Do you want to update this product?",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3bd848',
            confirmButtonText: 'Yes, update it!'
        }).then(result => {
            if (result.isConfirmed) {
                const formData = new FormData();
                formData.append("name", name.val());
                formData.append("price", price.val());
                formData.append("description", description.val());
                formData.append("image", image[0].files[0]);
                formData.append("quantity", quantity.val());


                $.ajax({
                    url: `/product/update/${id}`,
                    method: "PUT",
                    headers: {
                        'Authorization': `Bearer ${TOKEN}`
                    },
                    data: formData,
                    contentType: false,
                    processData: false,
                    beforeSend: function () {
                        swal = Swal.fire({
                            title: 'Please wait ...',
                            text: "We're updating your product",
                            didOpen: () => {
                                Swal.showLoading()
                            },
                        })
                    },
                    success: function (data) {
                        swal.close();
                        Swal.fire({
                            title: 'Success!',
                            text: 'Your product has been updated.',
                            icon: 'success',
                            confirmButtonColor: '#3bd848',
                            confirmButtonText: 'OK',
                            timer: 2000
                        });

                        setTimeout(function () {
                            window.location.href = `/admin/category/${categoryId}`;
                        }, 2000)
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        console.log(jqXHR.responseText)
                        swal.close();
                        Swal.fire({
                            title: 'Error!',
                            text: 'Something went wrong.',
                            icon: 'error',
                            confirmButtonColor: '#3bd848',
                            confirmButtonText: 'OK'
                        });
                    }
                })
            }
        })
    })
})