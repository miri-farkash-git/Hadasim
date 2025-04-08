import axios from "axios";
const baseUrl="http://localhost:8080/api/order"


export function getAllOrders(){
    return axios.get(baseUrl)
}
export function getOrdersBySupplierId(id){
    return axios.get(`${baseUrl}/${id}`)
}

export function getOrdersNotCompleted(){
    return axios.get(`${baseUrl}/InProgress`)
}

export function addOrder(order){
    return axios.post(baseUrl,order)
}

export function changeStatusForOrder(id, status) {
    return axios.put(`${baseUrl}/${id}`, { status })
}




