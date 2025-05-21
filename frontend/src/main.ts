import {createApp} from 'vue'
import App from './App.vue'
import routes from "./configs/routes.ts";
import {createRouter, createWebHistory} from 'vue-router'
import 'vant/es/toast/style'
import 'vant/es/notify/style'
import 'vant/es/dialog/style'
import {setAxiosRouter} from "./plugins/myAxios.ts";


const router = createRouter({
    history: createWebHistory(),
    routes,
})
const app = createApp(App);
// 注入路由实例
setAxiosRouter(router)
app.use(router);
app.mount('#app')
