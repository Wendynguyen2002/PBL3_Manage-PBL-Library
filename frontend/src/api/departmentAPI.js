import axios from './axiosConfig'

export const departmentAPI = {
    getAllDepartments: async (search = '', page = 0, size = 30, sortBy = 'id', sortDir = 'asc') => {
        const response = await axios.get('/departments', {
            params: { search, page, size, sortBy, sortDir }
        })
        return response.data // Page<DepartmentSummaryDTO>
    },

    getDepartmentById: async (id) => {
        const response = await axios.get(`/departments/${id}`)
        return response.data // DepartmentResponseDTO
    },

    createDepartment: async (data) => {
        const response = await axios.post('/departments', data)
        return response.data // DepartmentResponseDTO
    },

    updateDepartment: async (id, data) => {
        const response = await axios.put(`/departments/${id}`, data)
        return response.data // DepartmentResponseDTO
    },

    deleteDepartment: async (id) => {
        const response = await axios.delete(`/departments/${id}`)
        return response.data
    },

    getDepartmentsForDropdown: async () => {
        const response = await axios.get('/departments/dropdown')
        return response.data // List<DepartmentSummaryDTO>
    }
}