import axios from './axiosConfig'

export const projectAPI = {
    // Get all projects in a class
    getProjectsByPblClass: async (pblClassId) => {
        const response = await axios.get(`/pbl-classes/${pblClassId}/projects`)
        return response.data // List<ProjectSummaryDTO>
    },

    // Get available projects (same as getAll for now, but endpoint exists)
    getAvailableProjects: async (pblClassId) => {
        const response = await axios.get(`/pbl-classes/${pblClassId}/projects/available`)
        return response.data // List<ProjectSummaryDTO>
    },

    // Get specific project details
    getProjectById: async (pblClassId, projectId) => {
        const response = await axios.get(`/pbl-classes/${pblClassId}/projects/${projectId}`)
        return response.data // ProjectResponseDTO
    },

    // Create a new project (Lecturer only)
    createProject: async (pblClassId, data) => {
        const response = await axios.post(`/pbl-classes/${pblClassId}/projects`, data)
        return response.data // ProjectResponseDTO
    },

    // Update a project (Lecturer only)
    updateProject: async (pblClassId, projectId, data) => {
        const response = await axios.put(`/pbl-classes/${pblClassId}/projects/${projectId}`, data)
        return response.data // ProjectResponseDTO
    },

    // Delete a project (Lecturer only)
    deleteProject: async (pblClassId, projectId) => {
        const response = await axios.delete(`/pbl-classes/${pblClassId}/projects/${projectId}`)
        return response.data
    }
}