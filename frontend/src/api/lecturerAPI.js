import axios from './axiosConfig'

export const lecturerAPI = {
    getAllLecturers: async (search = '', page = 0, size = 30, sortBy = 'fullName', sortDir = 'asc') => {
        const response = await axios.get('/lecturers', {
            params: { search, page, size, sortBy, sortDir }
        })
        return response.data // Page<LecturerSummaryDTO>
    },

    getLecturerById: async (id) => {
        const response = await axios.get(`/lecturers/${id}`)
        return response.data // LecturerResponseDTO
    },

    createLecturer: async (data) => {
        const response = await axios.post('/lecturers', data)
        return response.data // LecturerResponseDTO
    },

    updateLecturer: async (id, data) => {
        const response = await axios.put(`/lecturers/${id}`, data)
        return response.data // LecturerResponseDTO
    },

    deleteLecturer: async (id) => {
        const response = await axios.delete(`/lecturers/${id}`)
        return response.data
    },

    resetLecturerPassword: async (id) => {
        const response = await axios.post(`/lecturers/${id}/reset-password`)
        return response.data // { message, lecturerId }
    },

    getOwnProfile: async () => {
        const response = await axios.get('/lecturers/profile')
        return response.data // LecturerResponseDTO
    },

    updateOwnProfile: async (data) => {
        const response = await axios.put('/lecturers/profile', data)
        return response.data // LecturerResponseDTO
    },

    changePassword: async (data) => {
        const response = await axios.put('/lecturers/profile/change-password', data)
        return response.data
    }
}