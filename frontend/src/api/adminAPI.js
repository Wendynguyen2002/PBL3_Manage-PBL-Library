import axios from './axiosConfig'

export const adminAPI = {
    getProfile: async () => {
        const response = await axios.get('/admin/profile')
        return response.data // AdminResponseDTO
    },

    changePassword: async (data) => {
        const response = await axios.post('/admin/change-password', data)
        return response.data
    }
}