import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";

import { getAllOrders, getOrdersBySupplierId, getOrdersNotCompleted } from "../api/Orders";
import Order from "../component/Order";
//import '../sass/orders.scss';  // עדכון כאן

function Orders() {
    let [status, setStatus] = useState()
    let [allOrders, SetAllOrders] = useState(true)
    let [filterOrders, setFilterOrders] = useState(false)
    let [arrOrders, setArrOrders] = useState([])
    let currentUser = useSelector(st => st.user.currentUser)
    let navigate = useNavigate()

    useEffect(() => {
        let isRelevant = true;
        setStatus("pending");
        if (currentUser == null)
            navigate('/login')
        else if (currentUser?.role == "ADMIN") {
            getAllOrders()
                .then(res => {
                    if (isRelevant) {
                        setArrOrders(res.data);
                        console.log(res.data);
                    }
                })
                .catch(err => {
                    if (isRelevant) {
                        console.log(err);
                    }
                })
                .finally(() => {
                    if (isRelevant) {
                        setStatus("finish");
                    }
                });
        }
        else {
            getOrdersBySupplierId(currentUser?._id)
                .then(res => {
                    if (isRelevant) {
                        setArrOrders(res.data);
                    }
                })
                .catch(err => {
                    if (isRelevant) {
                        console.log(err);
                    }
                })
                .finally(() => {
                    if (isRelevant) {
                        setStatus("finish");
                    }
                });
        }

        return () => {
            isRelevant = false;
        };
    }, [allOrders]);

    function fetchFilterOrders() {
        getOrdersNotCompleted()
            .then(res => {
                setArrOrders(res.data);
                console.log(res.data);
            })
            .catch(err => console.log(err));
    }
    

    return (
        <div className="orders-container">
            <h1>Orders:</h1>
            {status === "pending" && <p className="pending">Loading...</p>}
            {status === "finish" && arrOrders.length === 0 && (
                <p className="no-orders-message">No orders</p>
            )}
            {currentUser?.role == "ADMIN" && <>
                <button className="button" onClick={() => {
                        fetchFilterOrders()
                        setFilterOrders(true)
                    }} >Orders that have not yet been completed</button>
                    <br/>
                {filterOrders && <button className="button" onClick={() => { 
                    SetAllOrders(prev=>!prev)
                    setArrOrders(false) 
                    }} >all orders</button>}
                  <hr/>  
            </>}
            
            <div className="card-order">
            {arrOrders.length > 0 && arrOrders.map(order => (
                <div key={order._id} className="order-item">
                    {order && <Order order={order} />}
                </div>
            ))}</div>
        </div>
    );
}

export default Orders;
