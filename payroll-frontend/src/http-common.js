import axios from "axios";

const payrollBackendServer = "karting-app.eastus2.cloudapp.azure.com";
const payrollBackendPort = "8090";

console.log(payrollBackendServer)
console.log(payrollBackendPort)

export default axios.create({
    baseURL: `http://${payrollBackendServer}:${payrollBackendPort}/api`,
    headers: {
        'Content-Type': 'application/json'
    }
});