import { createRouter, createWebHistory } from 'vue-router';
import Login from "../components/Login.vue";
import AdminLayout from "../layouts/AdminLayout.vue";
import UserManage from "../components/UserManage.vue";
import TeamManage from "../components/TeamManage.vue";
const routes = [
    { path: '/login', component: Login },
    {
        path: '/dashboard',
        component: AdminLayout,
        children: [
            { path: 'user', component: UserManage },
            { path: 'team', component: TeamManage }
        ]
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

// 导航守卫：权限校验
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('admin-token');
    if (to.path !== '/login' && !token) {
        next('/login');
    } else {
        next();
    }
});

export default router;