$(document).ready(function () {
    const TOKEN = localStorage.getItem('token');
    const CATEGORY_ID = $("#categoryId").val();

    const btnAdd = $("#btnAdd");

    const btnDelete = $(".btn-delete");

    btnDelete.click(function (e) {
        e.preventDefault();

        const id = $(this).data("id");

        let swal = null;
        Swal.fire({
            title: 'Are your sure?',
            text: "You won't be able to revert this!\nAll products in this category will be deleted!",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#62b047',
            confirmButtonText: 'Yes, delete it!'
        }).then(result => {
            if (result.isConfirmed) {
                $.ajax({
                    url: `/category/delete/${id}`,
                    method: "DELETE",
                    headers: {
                        'Authorization': `Bearer ${TOKEN}`
                    },
                    beforeSend: function () {
                        swal = Swal.fire({
                            title: 'Please wait ...',
                            html: 'Deleting category',
                            didOpen: () => {
                                Swal.showLoading()
                            }
                        })
                    },
                    success: function (data) {
                        swal.close();
                        Swal.fire({
                            title: 'Deleted!',
                            text: 'Category has been deleted.',
                            icon: 'success',
                            confirmButtonColor: '#62b047',
                            confirmButtonText: 'OK',
                            timer: 2000
                        }).then(result => {
                            if (result.isConfirmed) {
                                window.location.href = "/admin/category";
                            }
                        });
                        setTimeout(function () {
                            window.location.href = "/admin/category";
                        }, 2000)
                    },
                    error: function (error) {
                        swal.close();
                        Swal.fire({
                            title: 'Error!',
                            text: 'Something went wrong.',
                            icon: 'error',
                            confirmButtonColor: '#62b047',
                            confirmButtonText: 'OK'
                        });
                    }
                })
            }
        })
    })

})