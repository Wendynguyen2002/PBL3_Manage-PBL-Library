import axios from './axiosConfig'

export const finalReportAPI = {
    // Student: Create or update their group's final report
    createOrUpdateReport: async (pblClassId, title, description, file) => {
        const formData = new FormData()

        const reportBlob = new Blob([JSON.stringify({ title, description })], {
            type: 'application/json'
        })
        formData.append('report', reportBlob)
        formData.append('file', file)

        const response = await axios.post(`/pbl-classes/${pblClassId}/final-reports`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        })
        return response.data // FinalReportResponseDTO
    },

    // Student: Get their group's report
    getMyGroupReport: async (pblClassId) => {
        const response = await axios.get(`/pbl-classes/${pblClassId}/final-reports/my-group`)
        return response.data // FinalReportResponseDTO
    },

    // Lecturer: Get all reports for this class
    getAllReportsForClass: async (pblClassId) => {
        const response = await axios.get(`/pbl-classes/${pblClassId}/final-reports`)
        return response.data // List<FinalReportSummaryDTO>
    },

    // Lecturer: Get specific report by ID
    getReportById: async (pblClassId, reportId) => {
        const response = await axios.get(`/pbl-classes/${pblClassId}/final-reports/${reportId}`)
        return response.data // FinalReportResponseDTO
    },

    // Download report file
    downloadReport: async (pblClassId, reportId) => {
        const response = await axios.get(`/pbl-classes/${pblClassId}/final-reports/${reportId}/download`, {
            responseType: 'blob'
        })
        return response
    },

    // Student: Toggle public status
    togglePublicStatus: async (pblClassId, reportId) => {
        const response = await axios.put(`/pbl-classes/${pblClassId}/final-reports/${reportId}/public-toggle`)
        return response.data
    }
}