import { useLocation, useNavigate } from "react-router-dom";
import '../sass/productDetails.scss';  // אל תשכח לייבא את קובץ ה-SASS

function ProductDetails() {

    let navigate = useNavigate();
    let location = useLocation();
    const product = location.state;

    if (!product) return <h2>Product not found</h2>;

    const handleGoBack = () => {
        navigate(-1); // יחזור לדף הקודם
    };


    return (
        <>

            <div className="product-details-container">
                <button className="close-button" onClick={handleGoBack}>X</button>
                <h1 className="product-details-header">{product?.name}</h1>
                <div className="product-details-info">
                    <h2>Price</h2>
                    <p>{product?.price} ₪</p>

                    <h2>Description</h2>
                    <p>{product?.description}</p>

                    <h2>Minimum Quantity</h2>
                    <p>{product?.minimumQuantity}</p>
                </div>
            </div></>
    );
}

export default ProductDetails;
