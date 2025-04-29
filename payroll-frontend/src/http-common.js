import axios from "axios";

const payrollBackendServer = import.meta.env.VITE_PAYROLL_BACKEND_SERVER;

console.log(payrollBackendServer)


export default axios.create({
    baseURL: `http://${payrollBackendServer}:8090/api`,
    headers: {
        'Content-Type': 'application/json'
    }
});