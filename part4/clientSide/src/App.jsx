import { useState } from 'react'
import './App.css'

// import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
// import NavBar from "./NavBar";
// import Supplier from "./Supplier";

import Suppliers from './pages/Suppliers';

import ProductDetails from './component/ProductDetails';
import Products from './component/Products';
import { Link, Route, Routes } from 'react-router-dom';
import Login from './component/Login';
import Logingrocer from './component/Logingrocer';
import SignUp from './component/SignUp';
import Orders from './pages/Orders';
import OrderForm from './component/OrderForm';
import SignOut from './component/SignOut';
import NavBar from './component/NavBar';
import Welcome from './component/Welcome';


function App() {
  return (
    <>
     
      <NavBar />
      <Routes>
        <Route path='/' element={<Welcome />} />
        <Route path='/orders' element={<Orders />} />
        <Route path="/suppliers" element={<Suppliers />} />
        <Route path='/login' element={<Login />} />
        <Route path='/loginGrocer' element={<Logingrocer />} />
        <Route path='/signUp' element={<SignUp />} />
        <Route path='/signOut' element={<SignOut />} />
        <Route path='/order-form' element={<OrderForm />} />
        <Route path="/Product" element={<Products />} />
        <Route path="/product/:productId" element={<ProductDetails />} />
      </Routes>
    </>
  );
}

export default App;

