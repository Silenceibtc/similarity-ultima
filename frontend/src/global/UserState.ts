import {UserType} from "../models/user";
import {ref} from "vue";

const currentUser = ref<UserType | null>(null);

const getCurrentUser = () => {
    return currentUser;
}

const setCurrentUser = (user: UserType) => {
    currentUser.value = user;
}

export { getCurrentUser, setCurrentUser };