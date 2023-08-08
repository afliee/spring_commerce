$(document).ready(function () {
    const TOKEN = localStorage.getItem("token");

    const categories = $(".categories");
    fetchAllCategories();

    function fetchAllCategories(page = 0, size = 12) {
        $.ajax({
            url: `/category/get?page=${page}&size=${size}`,
            type: "GET",
            headers: {
                'Authorization': `Bearer ${TOKEN}`
            },
            success: function (response) {
                const {content, number, totalPages} = response;
                
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
                console.log(error.responseJSON.message);
            }
        })
    }

    function registerPaginationEvent() {
        $(".page-item").click(function () {
            const page = $(this).attr("page");
            fetchAllCategories(page);
        })
    }

    function registerCategoryItemEvent() {
        $(".category-item").click(function () {
            const categoryId = $(this).data("id");
            window.location.href = `/admin/category/${categoryId}`;
        })
    }
})