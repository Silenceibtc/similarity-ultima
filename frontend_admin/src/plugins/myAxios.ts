import axios from "axios";

const isDev = process.env.NODE_ENV === "development";
const myAxios = axios.create({
    baseURL: isDev ? 'http://localhost:8080/api' : 'https://similarity-backend-157355-7-1323820062.sh.run.tcloudbase.com/api',
    withCredentials: true
});
myAxios.defaults.withCredentials = true;

myAxios.interceptors.response.use(
    (response) => {
        const res = response.data;
        return res;
    },
);

export default myAxios;