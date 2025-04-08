import { useForm } from "react-hook-form";
import { useLocation, useNavigate } from "react-router-dom";
import { useState } from "react";
import { addOrder } from "../api/Orders";
import "../sass/orderForm.scss";  // קובץ ה-SASS

function OrderForm() {
    let location = useLocation();
    let navigate = useNavigate();
    let { products } = location.state || {};
    let { supplierId } = location.state || null;
    
    const { register, handleSubmit, formState: { errors } } = useForm();
    const [orderedGoods, setOrderedGoods] = useState([]);

    const onSubmit = (data) => {
        const selectedProducts = products
            .map((p, index) => {
                const quantity = Number(data[`quantity_${index}`]) || 0;

                if (quantity < p.minimumQuantity) {
                    alert(`Quantity for ${p.name} should be at least ${p.minimumQuantity}`);
                    return null;
                }

                return {
                    name: p.name,
                    productId: p._id,
                    amount: quantity
                };
            })
            .filter(p => p !== null);

        if (selectedProducts.length === 0) {
            alert("Please select at least one product with a valid quantity.");
            return;
        }

        const orderData = {
            VendorCode: supplierId,
            OrderedGoods: selectedProducts
        };

        addOrder(orderData).then(res => {
            console.log("Order created successfully", res.data);
            navigate("/orders");
        }).catch(err => {
            console.error("Failed to create order", err.response);
        });
    };

    return (
        <div className="order-form-container">
            <h1>Create New Order</h1>
            <form onSubmit={handleSubmit(onSubmit)}>
                <ul>
                    {products.map((p, index) => (
                        <li key={p._id} className="product-item">
                            <p>
                                <span className="product-name">{p.name}</span> - ${p.price} 
                                <span className="min-quantity"> (Min: {p.minimumQuantity})</span>
                            </p>
                            <input
                                {...register(`quantity_${index}`, {
                                    valueAsNumber: true,
                                    min: { value: p.minimumQuantity, message: `Minimum order quantity is ${p.minimumQuantity}` }
                                })}
                                type="number"
                                placeholder="Quantity"
                                min={p.minimumQuantity}
                                step="1"
                                className={errors[`quantity_${index}`] ? "input-error" : ""}
                            />
                            {errors[`quantity_${index}`] && <p className="error-msg">{errors[`quantity_${index}`].message}</p>}
                        </li>
                    ))}
                </ul>
                <button type="submit">Submit Order</button>
            </form>
        </div>
    );
}

export default OrderForm;
