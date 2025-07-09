import { useSelector } from "react-redux"
import type { RootState } from "~/store"
import { disableLoading, setLoading } from "~/store/slice/commonSlince"
import { useDispatch } from "react-redux"
import $axios from "~/axios"
import { setUser, type UserLogin } from "~/store/slice/userDataSlice"
export default function commonHook () {

    const permissions = useSelector((state: RootState) =>  state.permissionSlice.permissions)
    const dispatch = useDispatch()

    const checkPermission = (resource: string, action: string) => {
        let item = permissions.find((item: any) => {
            if (item.resourceName === resource && item.actionNames.includes(action)) {
                return true
            }
        })
        if (item) {
            return true
        }
        
        return false
    }

    const fetchUserInfo = async (fetchPermission?: () => void): Promise<boolean> => {
        let auth = false
        dispatch(setLoading())
        await $axios.get("/user/profile").then((res: any) => {
            const result = res.data.result;

            const userDataLoad: UserLogin = {
                id: result.id || 0,
                fullname: result.fullName || "",
                email: result.email || "",
                phoneNumber: result.phoneNumber || "",
                avatarUrl: result.avatarUrl || "",
                roles: result.roles || [],
                loading: false,
            };
            dispatch(setUser(userDataLoad));
            if (fetchPermission) {
                fetchPermission()
            }
            auth = true
        }).catch ((err: any) => {
            console.log("Init user error ", err);
        }).finally(() => {
            dispatch(disableLoading())
        })
        return auth
    }

    return {
        checkPermission: checkPermission,
        fetchUserInfo: fetchUserInfo
    }
}