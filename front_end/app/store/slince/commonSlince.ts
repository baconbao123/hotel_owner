import { createSlice } from '@reduxjs/toolkit';

const commonSlice = createSlice({
    name: 'common',
    initialState: {
        loading: false,
    },
    reducers: {
        setLoading: (state) => {
            state.loading = true;
        },
        disableLoading: (state) => {
            state.loading = false
        }
    },
});

export const {
    setLoading,
    disableLoading,
} = commonSlice.actions;
export default commonSlice.reducer;
