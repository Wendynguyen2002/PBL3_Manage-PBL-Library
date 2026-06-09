import axios from './axiosConfig'

export const pblClassAPI = {
    // Get all PBL classes for current user (based on role)
    getMyPblClasses: async () => {
        const response = await axios.get('/pbl-classes')
        return response.data // List<PblClassSummaryDTO>
    },

    // Get PBL class by ID with full details
    getPblClassById: async (pblClassId) => {
        const response = await axios.get(`/pbl-classes/${pblClassId}`)
        return response.data // PblClassResponseDTO
    },

    // Create new PBL class (Lecturer only)
    createPblClass: async (data) => {
        const response = await axios.post('/pbl-classes', data)
        return response.data // PblClassResponseDTO
    },

    // Update PBL class (Lecturer only)
    updatePblClass: async (pblClassId, data) => {
        const response = await axios.put(`/pbl-classes/${pblClassId}`, data)
        return response.data // PblClassResponseDTO
    },

    // Delete PBL class (Lecturer or Admin)
    deletePblClass: async (pblClassId) => {
        const response = await axios.delete(`/pbl-classes/${pblClassId}`)
        return response.data
    },

    // Get enrolled students in a class
    getEnrolledStudents: async (pblClassId) => {
        const response = await axios.get(`/pbl-classes/${pblClassId}/students`)
        return response.data // List<StudentSummaryDTO>
    },

    // Get available students that can be added to class (Lecturer only)
    getAvailableStudents: async (pblClassId) => {
        const response = await axios.get(`/pbl-classes/${pblClassId}/available-students`)
        return response.data // List<StudentSummaryDTO>
    },

    // Get specific student details in a class
    getStudentInClass: async (pblClassId, studentId) => {
        const response = await axios.get(`/pbl-classes/${pblClassId}/students/${studentId}`)
        return response.data // StudentResponseDTO
    },

    // Add students to class (Lecturer only)
    addStudentsToClass: async (pblClassId, studentIds) => {
        const response = await axios.post(`/pbl-classes/${pblClassId}/students`, studentIds)
        return response.data
    },

    // Remove student from class (Lecturer only)
    removeStudentFromClass: async (pblClassId, studentId) => {
        const response = await axios.delete(`/pbl-classes/${pblClassId}/students/${studentId}`)
        return response.data
    }
}