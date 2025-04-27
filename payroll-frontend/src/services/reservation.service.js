import httpClient from "../http-common";

const getAll = () => {
    return httpClient.get("/reservation/all");
};

const getById = (id) => {
    return httpClient.get(`/reservation/${id}`);
};

const create = (data, isAdmin, customPrice, specialDiscount ) => {
    const params = new URLSearchParams();
    if (isAdmin !== null) params.append("isAdmin", isAdmin);
    if (customPrice !== null) params.append("customPrice", customPrice);
    if (specialDiscount !== null) params.append("specialDiscount", specialDiscount);

    return httpClient.post(`/reservation/?${params.toString()}`, data);
};

const update = (data) => {
    return httpClient.put("/reservation/", data);
};

const remove = (reservationDate) => {
    return httpClient.delete(`/reservation/${reservationDate}`);
  };

const getByDate = (reservationDate) => {
    return httpClient.get(`/reservation/date/${reservationDate}`);
};

const getIncomeFromLapsOrTime = (startDate, endDate) => {
    return httpClient.get("/reservation/income-lapsOrTime", {
        params: {
            startDate,
            endDate
        }
    });
};

const getIncomePerPerson = (startDate, endDate) => {
    return httpClient.get("/reservation/income-persons", {
        params: {
            startDate,
            endDate
        }
    });
};

const getAllReservationsByDuration = () => {
    return httpClient.get("/reservation/allByDuration");
  };
  

export default {
    getAll,
    getById,
    create,
    update,
    remove,
    getByDate,
    getIncomeFromLapsOrTime,
    getIncomePerPerson,
    getAllReservationsByDuration
};
