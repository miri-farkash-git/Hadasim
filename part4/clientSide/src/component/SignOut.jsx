import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { signOut } from "../features/UserSlice";

const SignOut = () => {
    const dispatch = useDispatch();
    let navigate = useNavigate();

    const handleSignOut = () => {
        dispatch(signOut());
        navigate('/'); // מפנה לדף הבית
    };

    return (
        <>
    
            <button className="signOut-button" onClick={handleSignOut}>
            If you are sure you want to log out click here
            </button></>
    );
};

export default SignOut;