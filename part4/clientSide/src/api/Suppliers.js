import axios from "axios";

const baseUrl="http://localhost:8080/api/supplier"

export function getAllSuppliers(){
    return axios.get(baseUrl)
}


export function login({password,phone}){
    return axios.post(`${baseUrl}/login`,{password,phone})
}


export function signUp(supplier){
    return axios.post(baseUrl,supplier)
}



