import httpClient from "../http-common";

const save = (data) => {
    return httpClient.post("/kart/save", data);
};

const getByAvailability = (availability) => {
    return httpClient.get(`/kart/available/${availability}`);
};

const update = (data) => {
    return httpClient.put("/kart/update", data);
};

const remove = (id) => {
    return httpClient.delete(`/kart/delete/${id}`);
};

export default {
    save,
    getByAvailability,
    update,
    remove
};
