import axios from './axiosConfig'

export const taskSubmissionAPI = {
    // Student: Submit or update their group's submission
    submitOrUpdateSubmission: async (pblClassId, taskId, data) => {
        const response = await axios.post(`/pbl-classes/${pblClassId}/tasks/${taskId}/submissions`, data)
        return response.data // TaskSubmissionResponseDTO
    },

    // Student: Get their group's submission
    getMyGroupSubmission: async (pblClassId, taskId) => {
        const response = await axios.get(`/pbl-classes/${pblClassId}/tasks/${taskId}/submissions/my-group`)
        return response.data // TaskSubmissionResponseDTO
    },

    // Lecturer: Get all submissions for a task
    getAllSubmissionsForTask: async (pblClassId, taskId) => {
        const response = await axios.get(`/pbl-classes/${pblClassId}/tasks/${taskId}/submissions`)
        return response.data // List<TaskSubmissionSummaryDTO>
    },

    // Lecturer: Get a specific group's submission details
    getSubmissionByGroup: async (pblClassId, taskId, groupId) => {
        const response = await axios.get(`/pbl-classes/${pblClassId}/tasks/${taskId}/submissions/groups/${groupId}`)
        return response.data // TaskSubmissionResponseDTO
    }
}