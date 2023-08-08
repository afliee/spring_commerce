$(document).ready(function () {
    const TOKEN = localStorage.getItem('token');

    const searchDocs = $("#search-docs");
    const btnSearch = $(".btn-search");
    const categories = $(".categories");


    btnSearch.on('click', function (e) {
        e.preventDefault();
        const searchValue = searchDocs.val();

        fetchCategory({
            filter: searchValue,
            page: 0,
            size: 12
        })
    });


    function fetchCategory(
        {
            filter = '',
            page = 0,
            size = 12

        }
    ) {
        let swal = null;

        $.ajax({
            url: `/category/get?filter=${filter}&page=${page}&size=${size}`,
            type: 'GET',
            headers: {
                'Authorization': `Bearer ${TOKEN}`
            },
            beforeSend: function (xhr) {
                swal = Swal.fire({
                    title: 'Loading...',
                    text: 'Please wait ...',
                    didOpen: () => {
                        Swal.showLoading()
                    }
                })
            },
            success: function (data) {
                swal.close();

                const {content, number, totalPages} = data;

                categories.empty();

                content.forEach(category => {
                    categories.append(`
                        <div class="col-md-4 category-item" data-id="${category.id}">
                            <div class="card mb-4 shadow-sm border-end">
                                <div class="card-body">
                                    <h4 class="card-title">${category.name}</h4>
                                    <p class="card-text text-truncate">${category.description}</p>
                                    <i><strong>Create At</strong></i> <span> : ${category.createdAt ? category.createdAt : 'No range'}</span>
                                </div>
                        </div>
                    `);
                })

                $(".pagination").empty();
                const paginationOptions = generatePaginationOptions(number + 1, totalPages);
                $(".pagination").append(paginationOptions);
                registerPaginationEvent();
                registerCategoryItemEvent();
            },
            error: function (error) {
                swal.close();
                console.log(error.responseJSON.message);
                Swal.fire({
                    icon: 'error',
                    title: 'Oops...',
                    text: 'Something went wrong!',
                    timer: 2000
                });
            }
        })
    }

    function registerPaginationEvent() {
        $(".page-item").click(function () {
            const page = $(this).attr("page");
            const filter = searchDocs.val();
            fetchCategory({
                filter: filter,
                page: page
            });
        })
    }

    function registerCategoryItemEvent() {
        $(".category-item").click(function () {
            const categoryId = $(this).data("id");
            window.location.href = `/admin/category/${categoryId}`;
        })
    }
})