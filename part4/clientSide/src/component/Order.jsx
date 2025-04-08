import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { useEffect } from "react";
import { changeStatusForOrder } from "../api/Orders";
import '../sass/order.scss';  // עדכון כאן

function Order({ order }) {
    let currentUser = useSelector(st => st.user.currentUser);
    let navigate = useNavigate();

    useEffect(() => {
        if (currentUser == null) {
            navigate('/login'); // הפנייה לדף הלוגין אם המשתמש לא מחובר
        }
    }, [currentUser, navigate]);

    function changeStatus() {
        
        let status = currentUser.role === "ADMIN" ? "ACCEPTED" : "APPROVED";
        changeStatusForOrder(order._id, status).then(res =>
            console.log(res),
            alert("change status")
        ).catch(err => {
            console.log(err);
        });      
    }

    return (
        <div className="order-container">
            <h2>Status: {order.status}</h2>
            <button onClick={changeStatus}>Change Status</button>

            {/* תצוגת פרטי ההזמנה */}
            <div className="order-details">
                <p><strong>Order Date:</strong> {new Date(order.orderDate).toLocaleDateString()}</p>
                <p><strong>Deadline:</strong> {new Date(order.deadline).toLocaleDateString()}</p>
                {currentUser?.role=="ADMIN"&&<p><strong>Vendor ID:</strong> {order.VendorCode}</p>}
            </div>

            <ul>
                {order.OrderedGoods.map(prod => {
                    return (
                        <li key={prod._id}>
                            <strong>Name:</strong> {prod.name} <strong>Amount:</strong> {prod.amount}
                        </li>
                    );
                })}
            </ul>
        </div>
    );
}

export default Order;
