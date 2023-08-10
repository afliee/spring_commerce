$(document).ready(function () {
    const products = $(".product");
    console.log("products", products);
    products.each(function () {
        const product = $(this);

        const price = $(this).find(".price-pr").text().trim().replace("$", "");
        const amount = product.find("#amount");

        amount.on('change', function () {
            const value = $(this).val();

            const total = parseFloat(price) * parseInt(value);
            product.find(".total_price").text("$" + total.toFixed(1));
        })

    })
})