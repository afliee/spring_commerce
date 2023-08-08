$(document).ready(function ( ) {
    const TOKEN = localStorage.getItem("token");
    const CATEGORY_ID = $("#categoryId").val();

    const priceInput = $("#price");
    const quantityInput = $("#quantity");
    const nameInput = $("#name");
    const descriptionInput = $("#description");
    const imageFileInput = $("#image");

    const btnCreate = $(".btn-create");

    priceInput.change(function (e) {
        const value = $(this).val();
        if (value < 1) {
            $(this).val(1);
        }
    });

    quantityInput.change(function (e) {
        const value = $(this).val();
        if (value < 1) {
            $(this).val(1);
        }
    });

    btnCreate.click(function (e) {
        e.preventDefault();
        let swal = null;
        Swal.fire({
            title: "Are you sure?",
            text: "Do you want to create this product?",
            icon: "warning",
            showCancelButton: true,
            confirmButtonText: "Yes, create it!",
            cancelButtonText: "Cancel",
            confirmButtonColor: "#61d535",
        }).then(result => {
            if (result.isConfirmed) {
                // file of input
                formData = new FormData();
                formData.append("name", nameInput.val());
                formData.append("price", priceInput.val());
                formData.append("quantity", quantityInput.val());
                formData.append("description", descriptionInput.val());
                formData.append("image", imageFileInput[0].files[0]);
                formData.append("categoryId", CATEGORY_ID);

                $.ajax({
                    url: '/product/create',
                    type: 'POST',
                    headers: {
                        'Authorization': `Bearer ${TOKEN}`
                    },
                    data: formData,
                    enctype: 'multipart/form-data',
                    contentType: false,
                    processData: false,
                    beforeSend: function () {
                        swal = Swal.fire({
                            title: "Please wait...",
                            text: "Creating product...",
                            showConfirmButton: false,
                            allowOutsideClick: false,
                            willOpen: () => {
                                Swal.showLoading();
                            }
                        })
                    },
                    success: function (data) {
                        swal.close();
                        Swal.fire({
                            title: "Success!",
                            text: 'Successfully created product!',
                            icon: "success",
                            confirmButtonColor: "#61d535",
                            timer: 1500
                        });
                        setTimeout(function () {
                            window.location.href = "/admin/category/" + CATEGORY_ID;
                        }, 1500);
                    },
                    error: function (err) {
                        console.log(err)
                        swal.close();
                        Swal.fire({
                            title: "Error!",
                            text: err.responseJSON.message,
                            icon: "error",
                            confirmButtonColor: "#61d535",
                            timer: 1500
                        });
                    }
                })
            }
        })
    })
})