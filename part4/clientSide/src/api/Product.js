import axios from "axios";
const baseUrl="http://localhost:8080/api/product"


export function getProductDeatailsById(id){
    return axios.get(`${baseUrl}//product/${id}`)
}