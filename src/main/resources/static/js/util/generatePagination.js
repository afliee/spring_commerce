function generatePaginationOptions(currentPage, totalPages, MAX_PAGE = 5) {
    let pagination = '';

    //  if totalPage > MAX_PAGE => show MAX_PAGE
    // else the distance between startPage and endPage is MAX_PAGE
    let startPage = 1;
    let endPage = totalPages;

    if (totalPages > MAX_PAGE) {
        const distance = Math.floor(MAX_PAGE / 2);
        startPage = currentPage - distance;
        endPage = currentPage + distance;

        if (startPage < 1) {
            endPage += Math.abs(startPage) + 1;
            startPage = 1;
        }

        if (endPage > totalPages) {
            startPage -= endPage - totalPages;
            endPage = totalPages;
        }
    }

    console.log("startPage", startPage);
    console.log("endPage", endPage);

    for (let i = startPage; i <= endPage; i++) {
        pagination += `
                <li data-page="${i}" class="page-item ${currentPage === i ? 'active' : ''}">
                    <span class="page-link">${i}</span>
                </li>
            `
    }

    return pagination;
}