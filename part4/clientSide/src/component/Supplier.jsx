import { useNavigate } from "react-router-dom";
import '../sass/supplier.scss'; // עדכון כאן

function Supplier({ supplier }) {
    let navigate = useNavigate();

    // בדיקה אם יש מידע על הספק
    if (!supplier) {
        return <p>No supplier data available</p>;
    }

    return (
        <div className="supplier-container">
            <div className="supplier-header">
                <h1>{supplier.companyName}</h1>
                <h2>{supplier.name}</h2>
                <h2>{supplier.phone}</h2>
                <h2>{supplier.representativeName}</h2>
            </div>
            
            <div className="supplier-info">
                <button 
                    onClick={() => navigate('/Product', { state: { products: supplier.goods, supplierId: supplier._id } })}
                >List of Products</button>
            </div>
        </div>
    );
}

export default Supplier;
