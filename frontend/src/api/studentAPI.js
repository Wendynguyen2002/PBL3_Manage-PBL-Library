import axios from './axiosConfig'

export const studentAPI = {
    getAllStudents: async (search = '', page = 0, size = 30, sortBy = 'fullName', sortDir = 'asc') => {
        const response = await axios.get('/students', {
            params: { search, page, size, sortBy, sortDir }
        })
        return response.data // Page<StudentSummaryDTO>
    },

    getStudentById: async (id) => {
        const response = await axios.get(`/students/${id}`)
        return response.data // StudentResponseDTO
    },

    createStudent: async (data) => {
        const response = await axios.post('/students', data)
        return response.data // StudentResponseDTO
    },

    updateStudent: async (id, data) => {
        const response = await axios.put(`/students/${id}`, data)
        return response.data // StudentResponseDTO
    },

    deleteStudent: async (id) => {
        const response = await axios.delete(`/students/${id}`)
        return response.data
    },

    resetStudentPassword: async (id) => {
        const response = await axios.post(`/students/${id}/reset-password`)
        return response.data // { message, studentId }
    },

    getOwnProfile: async () => {
        const response = await axios.get('/students/profile')
        return response.data // StudentResponseDTO
    },

    updateOwnProfile: async (data) => {
        const response = await axios.put('/students/profile', data)
        return response.data // StudentResponseDTO
    },

    changePassword: async (data) => {
        const response = await axios.put('/students/profile/change-password', data)
        return response.data
    }
}