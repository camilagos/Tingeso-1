import axios from "axios";

const payrollBackendServer = "karting-app.eastus2.cloudapp.azure.com";

console.log(payrollBackendServer)
console.log(payrollBackendPort)

export default axios.create({
    baseURL: `http://${payrollBackendServer}/api`,
    headers: {
        'Content-Type': 'application/json'
    }
});