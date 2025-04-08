import { useState } from "react";
import { useForm } from "react-hook-form";
import { useDispatch } from "react-redux";
import { signUp } from "../api/Suppliers";
import { signIn } from "../features/UserSlice";
import '../sass/login_signUp.scss';

function SignUp() {
    const { register, handleSubmit, formState: { errors } } = useForm();
    const dispatch = useDispatch();

    // סטייט לשמירת שורות דינמיות של מוצרים
    const [products, setProducts] = useState([{ name: '', description: '', price: '', minimumQuantity: '' }]);

    // פונקציה שמוסיפה שורה חדשה של מוצר
    const addProduct = () => {
        setProducts([...products, { name: '', description: '', price: '', minimumQuantity: '' }]);
    };

    const saveUser = (data) => {
        console.log(data);
        signUp(data).then(res => {
            console.log(res.data);
            dispatch(signIn(res.data));
        }).catch(err => {
            console.log(err.response);
        });
    };

    return (
        <form onSubmit={handleSubmit(saveUser)} className="login-form"> {/* הוספת className */}
            <h1>Sign Up</h1>

            <div className="input-group">
                <input
                    type="text"
                    placeholder="Company Name"
                    {...register("companyName", {
                        required: { value: true, message: "companyName is required" }
                    })}
                />
                {errors.companyName && <p>{errors.companyName.message}</p>}
            </div>

            <div className="input-group">
                <input
                    type="text"
                    placeholder="Phone"
                    {...register("phone", {
                        required: { value: true, message: "Phone is required" },
                        pattern: {
                            value: /^\d{9,10}$/,
                            message: "Phone number must be 9 or 10 digits"
                        }
                    })}
                />
                {errors.phone && <p>{errors.phone.message}</p>}
            </div>

            <div className="input-group">
                <input
                    type="text"
                    placeholder="Representative Name"
                    {...register("representativeName", {
                        required: { value: true, message: "Representative name is required" }
                    })}
                />
                {errors.representativeName && <p>{errors.representativeName.message}</p>}
            </div>

            <div className="input-group">
                <input
                    type="password"
                    placeholder="Password"
                    {...register("password", {
                        required: { value: true, message: "Password is required" },
                        minLength: {
                            value: 6,
                            message: "Password must be at least 6 characters"
                        }
                    })}
                />
                {errors.password && <p>{errors.password.message}</p>}
            </div>

            <div className="input-group">
                <input
                    type="text"
                    placeholder="role"
                    {...register("role", {
                        required: {
                            value: true,
                            message: "Role is required"
                        },
                        validate: value => 
                            value === 'ADMIN' || value === 'VENDOR' || "Role must be either ADMIN or VENDOR"
                    })}
                />
                {errors.role && <p>{errors.role.message}</p>}
            </div>

            {/* שמירת שורות דינמיות של מוצרים */}
            {products.map((p, index) => (
                <div key={index} className="input-group">
                    <input
                        type="text"
                        placeholder="Product Name"
                        {...register(`goods[${index}].name`, {
                            required: { value: true, message: "Product name is required" }
                        })}
                    />
                    {errors.goods?.[index]?.name && <p>{errors.goods[index].name.message}</p>}

                    <input
                        type="text"
                        placeholder="Description"
                        {...register(`goods[${index}].description`, {
                            required: { value: true, message: "Description is required" }
                        })}
                    />
                    {errors.goods?.[index]?.description && <p>{errors.goods[index].description.message}</p>}

                    <input
                        type="number"
                        placeholder="Price"
                        {...register(`goods[${index}].price`, {
                            required: { value: true, message: "Price is required" }
                        })}
                    />
                    {errors.goods?.[index]?.price && <p>{errors.goods[index].price.message}</p>}

                    <input
                        type="number"
                        placeholder="Minimum Quantity"
                        {...register(`goods[${index}].minimumQuantity`, {
                            required: { value: true, message: "Minimum quantity is required" }
                        })}
                    />
                    {errors.goods?.[index]?.minimumQuantity && <p>{errors.goods[index].minimumQuantity.message}</p>}
                </div>
            ))}

            {/* כפתור להוספת שורה חדשה של מוצר */}
            <button type="button" onClick={addProduct}>Add Product</button>
            <hr/>
            <input type="submit" value="Submit" className="submit-btn" />
        </form>
    );
}

export default SignUp;
