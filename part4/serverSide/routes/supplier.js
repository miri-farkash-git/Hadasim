import { Router } from "express";
import { getAllVendors, getVendorrById, login, signUp } from "../controller/supplier.js";



const router = Router();

router.get("/", getAllVendors);
router.get("/:id", getVendorrById);
router.post("/", signUp);
router.post("/login", login);



export default router;