import axios from './axiosConfig'

export const progressTaskAPI = {
    // Get all tasks in a class
    getTasksByClass: async (pblClassId) => {
        const response = await axios.get(`/pbl-classes/${pblClassId}/tasks`)
        return response.data // List<ProgressTaskSummaryDTO>
    },

    // Get single task details
    getTaskById: async (pblClassId, taskId) => {
        const response = await axios.get(`/pbl-classes/${pblClassId}/tasks/${taskId}`)
        return response.data // ProgressTaskResponseDTO
    },

    // Lecturer: Create task
    createTask: async (pblClassId, data) => {
        const response = await axios.post(`/pbl-classes/${pblClassId}/tasks`, data)
        return response.data // ProgressTaskResponseDTO
    },

    // Lecturer: Update task
    updateTask: async (pblClassId, taskId, data) => {
        const response = await axios.put(`/pbl-classes/${pblClassId}/tasks/${taskId}`, data)
        return response.data // ProgressTaskResponseDTO
    },

    // Lecturer: Delete task
    deleteTask: async (pblClassId, taskId) => {
        const response = await axios.delete(`/pbl-classes/${pblClassId}/tasks/${taskId}`)
        return response.data
    }
}