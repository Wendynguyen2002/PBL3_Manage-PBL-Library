import axios from './axiosConfig'

export const majorAPI = {
    getAllMajors: async (search = '', page = 0, size = 30, sortBy = 'id', sortDir = 'asc') => {
        const response = await axios.get('/majors', {
            params: { search, page, size, sortBy, sortDir }
        })
        return response.data // Page<MajorSummaryDTO>
    },

    getMajorById: async (id) => {
        const response = await axios.get(`/majors/${id}`)
        return response.data // MajorSummaryDTO
    },

    createMajor: async (data) => {
        const response = await axios.post('/majors', data)
        return response.data // MajorSummaryDTO
    },

    updateMajor: async (id, data) => {
        const response = await axios.put(`/majors/${id}`, data)
        return response.data // MajorSummaryDTO
    },

    deleteMajor: async (id) => {
        const response = await axios.delete(`/majors/${id}`)
        return response.data
    },

    getAllMajorsList: async () => {
        const response = await axios.get('/majors', {
            params: { page: 0, size: 1000 }
        })
        return response.data.content // List<MajorSummaryDTO>
    }
}