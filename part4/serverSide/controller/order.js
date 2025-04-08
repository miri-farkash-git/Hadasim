import { orderModel } from "../models/order.js";
import { vendorModel } from "../models/supplier.js";



export async function getAllOrders(req, res) {
    try {
        let result = await orderModel.find()
        if (result.lenght == 0)
            res.json("no orders")
        else
            res.json(result)
    }
    catch (err) {
        res.status(400).send({ title: "Failed to bring all the orders", message: err.message })
    }
};


export async function getOrderByVentorId(req, res) {
    let { id } = req.params;
    try {
        let result = await orderModel.find({ VendorCode: id })
        if (!result)
            return res.status(404).send({ title: "Problem with the vendor id", message: "we dont have orders for this vendor" })
        res.json(result)
    }
    catch (err) {
        res.status(400).send({ title: "Failed to bring  the orders by vendor id", message: err.message })
    }
}


export async function addOrder(req, res) {
   
    let { body } = req;
    console.log(body);
    if (!body.VendorCode || !body.OrderedGoods)
        return res.status(404).send({ title: "Missing data.", message: "VendorCode and OrderedGoods are required" });

    try {
        const vendor = await vendorModel.findOne({ _id: body.VendorCode });
        if (!vendor) {
            return res.status(400).send({
                title: "Error, the order cannot be completed",
                message: "Vendor not found"
            });
        }

        for (const orderedItem of body.OrderedGoods) {
            const foundProduct = vendor.goods.find(vendorProduct => {
                return vendorProduct._id.toString() === orderedItem.productId.toString(); 
            });

            if (!foundProduct) {
                return res.status(400).send({
                    title: "Error, the order cannot be completed",
                    message: `Product ${orderedItem.product} not found in vendor's inventory`,
                });
            }

            if (orderedItem.amount < foundProduct.minimumQuantity) {
                return res.status(400).send({
                    title: "Error, the order cannot be completed",
                    message: `Amount for product ${orderedItem.product} is less than the minimum purchase quantity: ${foundProduct.minimumQuantity}`, // תיקון כאן
                });
            }
        }


       

        // יצירת ההזמנה ושמירה במסד הנתונים
        let newOrder = new orderModel(body);
        let data = await newOrder.save();

        res.json(data);
    }
    catch (err) {
        res.status(400).send({ title: "Failed to add the order", message: err.message });
    }
}



export async function updateOrder_status(req, res) {
    let { id } = req.params
    let { status } = req.body
   

    try {
        let result = await orderModel.findByIdAndUpdate(id, { status: status }, { new: true })
        console.log(result);
        if (!result)
            return res.status(404).send({ title: "cannot update this order", message: "no order with such id" })
        res.status(200).json({ title: "changed", message: "we changes successfully" })
    }
    catch (err) {
        res.status(400).send({ title: "Failed to update the status order", message: err.message })
    }

}


export async function getAllOrdersInProgress(req, res) {
    try {
        let result = await orderModel.find({
            $or: [{ status: 'PENDING' }, { status: 'APPROVED' }]
        });


        if (result.lenght == 0)
            res.json("There is no order that has not yet been received.")
        else
            res.json(result)
    }
    catch (err) {
        res.status(400).send({ title: "Failed to bring the orders that not  received", message: err.message })
    }
};








