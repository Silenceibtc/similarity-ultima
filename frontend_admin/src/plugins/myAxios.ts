import axios from "axios";
import {Router} from "vue-router";

let globalRouter: Router;

export function setAxiosRouter(router: Router) {
    globalRouter = router;
}

const isDev = process.env.NODE_ENV === "development";
const myAxios = axios.create({
    baseURL: isDev ? 'http://localhost:8080/api' : 'https://similarity-backend-157355-7-1323820062.sh.run.tcloudbase.com/api',
    withCredentials: true
});
myAxios.defaults.withCredentials = true;

myAxios.interceptors.response.use(
    (response) => {
        const res = response.data;
        if (res.code === 4010) {
            globalRouter.push('/user/login');
        }
        return res;
    },
);

export default myAxios;