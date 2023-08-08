$(document).ready(function () {
    const TOKEN = localStorage.getItem("token");
    const btnCreate = $(".btn-create");

    btnCreate.click(function (e) {
        e.preventDefault();

        const name = $("#name").val();
        const description = $("#description").val();

        const data = {
            name: name,
            description: description
        };

        Swal.fire({
            title: 'Are you sure?',
            text: "You want to create this category?",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#15a362',
            confirmButtonText: 'Yes, create it!'
        }).then(result => {
            if (result.isConfirmed) {
                $.ajax({
                    url: "/category/create",
                    type: "POST",
                    headers: {
                        "Authorization": `Bearer ${TOKEN}`
                    },
                    contentType: "application/json",
                    data: JSON.stringify(data),
                    success: function (response) {
                        Swal.fire({
                            title: 'Success!',
                            text: 'You created a new category',
                            icon: 'success',
                            confirmButtonColor: '#15a362',
                            confirmButtonText: 'OK'
                        }).then(result => {
                            if (result.isConfirmed) {
                                location.href = "/admin/category ";
                            }
                        })
                    },
                    error: function (response) {
                        Swal.fire({
                            title: 'Error!',
                            text: 'You cannot create a new category',
                            icon: 'error',
                            confirmButtonColor: '#15a362',
                            confirmButtonText: 'OK'
                        })
                    }
                })
            }
        })
    });
})