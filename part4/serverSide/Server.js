import express from "express";
import cors from "cors";
import dotenv from "dotenv";

import { connectToDb } from "./config/db.js";
import productRouter from "./routes/product.js";
import orderRouter from "./routes/order.js";
import supplierRouter from "./routes/supplier.js";


dotenv.config();
connectToDb();

const app = express();

app.use(express.json());
app.use(cors());

app.use("/api/product", productRouter);
app.use("/api/supplier", supplierRouter);
app.use("/api/order", orderRouter);

const port = process.env.PORT;

app.listen(port, () => { console.log("app is listening on port " + port) })

