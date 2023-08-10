$(document).ready(function () {
    const carts = $(".cart");
    const products = [];
    const cartList = $(".cart-list");
    const total = $(".total");
    const cookies = document.cookie.split(';');
    let price = 0;

    const productIds = cookies.filter(cookie => cookie.includes("products"));
    console.log("cookies" ,cookies)
    if (productIds.length > 0) {
        const ids = productIds[0].split("=")[1];
        ids.split("/").forEach(id => {
            if (!id) {
                return;
            }
            findProduct(id);
            products.push(id);
        })
        cartList.append(`
                    <li class="total">
                        <a href="/cart" class="btn btn-default hvr-hover btn-cart">VIEW CART</a>
                        <span class="float-right"><strong>Total</strong>: ${'$' + price}</span>
                    </li>
                `)
    } else {

    }
    carts.on('click', function () {
        const productId = $(this).data("id");

        const cookies = document.cookie.split(';');

        //     filter cookies contains key : user
        const userCookies = cookies.filter(cookie => cookie.includes("user"));
        if (userCookies.length === 0) {
            products.push(productId);
            findProduct(productId);
            const time = 60 * 60 * 24 * 30; // 30 days
        //     update cookie value
// document.cookie = `products=${products};path=/;max-age=${time}`;
        //     set cookie
        } else {
            $.ajax({
                url: `/cart/add?productId=${productId}`,
                type: "POST",
                success: function (status) {
                    console.log("product added to cart", productId)
                },
                error: function (error) {
                    console.log(error.responseText);
                }
            })
        }
    })

    function findProduct(id) {
        console.log("findProduct", id)
        $.ajax({
            url: `/product/find?id=${id}`,
            type: "GET",
            success: function (product) {
                price += product.price;
                cartList.append(`
                        <li>
                            <a href="#" class="photo"><img src="${product.image}" class="cart-thumb" alt=""/></a>
                            <h6><a href="#">${product.name}</a></h6>
                            <p>1x - <span class="price">${product.price}</span></p>
                        </li>
                    `)
                if (cartList.find(".total").length > 0) {
                    cartList.find(".total").remove();
                    cartList.append(`
                        <li class="total">
                            <a href="/cart" class="btn btn-default hvr-hover btn-cart">VIEW CART</a>
                            <span class="float-right"><strong>Total</strong>: ${'$' + price}</span>
                        </li>
                    `)
                }
            },
            error: function (error) {
                console.log(error.responseText);
            }
        })
    }
})