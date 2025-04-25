import httpClient from "../http-common";

const save = (data) => {
    return httpClient.post("/user/", data);
};

const getById = (id) => {
    return httpClient.get(`/user/${id}`);
};

const update = (data) => {
    return httpClient.put("/user/", data);
};

const remove = (id) => {
    return httpClient.delete(`/user/${id}`);
};

const getByRut = (rut) => {
    return httpClient.get(`/user/rut/${rut}`);
};

const login = (data) => {
    return httpClient.post("/user/login", data);
};

export default {
    save,
    getById,
    update,
    remove,
    getByRut,
    login
};
