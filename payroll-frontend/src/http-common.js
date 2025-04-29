import axios from "axios";

const payrollBackendServer = import.meta.env.VITE_PAYROLL_BACKEND_SERVER;
const payrollBackendServerPort = import.meta.env.VITE_PAYROLL_BACKEND_PORT;

console.log(payrollBackendServer)
console.log(payrollBackendServerPort)

export default axios.create({
    baseURL: `http://${payrollBackendServer}:${payrollBackendServerPort}/api`,
    headers: {
        'Content-Type': 'application/json'
    }
});