import axios from './axiosConfig'

export const pblGroupAPI = {
    // Get all groups in a class
    getGroupsByClass: async (pblClassId) => {
        const response = await axios.get(`/pbl-classes/${pblClassId}/groups`)
        return response.data // List<PblGroupSummaryDTO>
    },

    // Create a new group (Student only)
    createGroup: async (pblClassId, projectId = null) => {
        const params = projectId ? { projectId } : {}
        const response = await axios.post(`/pbl-classes/${pblClassId}/groups`, null, { params })
        return response.data // PblGroupSummaryDTO
    },

    // Update group's project (Student only)
    updateGroupProject: async (pblClassId, groupId, projectId) => {
        const response = await axios.put(`/pbl-classes/${pblClassId}/groups/${groupId}/project`, null, {
            params: { projectId }
        })
        return response.data
    },

    // Disband group (Student only - only if they're the only member)
    disbandGroup: async (pblClassId, groupId) => {
        const response = await axios.delete(`/pbl-classes/${pblClassId}/groups/${groupId}/disband`)
        return response.data
    },

    // Join a group (Student only)
    joinGroup: async (pblClassId, groupId) => {
        const response = await axios.post(`/pbl-classes/${pblClassId}/groups/${groupId}/join`)
        return response.data
    },

    // Remove student from group (Lecturer only)
    removeStudentFromGroup: async (pblClassId, groupId, studentId) => {
        const response = await axios.delete(`/pbl-classes/${pblClassId}/groups/${groupId}/students/${studentId}`)
        return response.data
    },

    // Delete group (Lecturer only)
    deleteGroup: async (pblClassId, groupId) => {
        const response = await axios.delete(`/pbl-classes/${pblClassId}/groups/${groupId}`)
        return response.data
    }
}