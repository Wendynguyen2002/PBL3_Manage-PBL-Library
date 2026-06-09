import axios from './axiosConfig'

export const libraryAPI = {
    // Search reports with filters
    searchReports: async (searchParams) => {
        const response = await axios.post('/library/search', {
            keyword: searchParams.keyword || '',
            className: searchParams.className || '',
            departmentId: searchParams.departmentId || '',
            fileType: searchParams.fileType || '',
            sortBy: searchParams.sortBy || 'newest',
            page: searchParams.page || 0,
            size: searchParams.size || 20
        })
        return response.data // Page<LibraryReportResponseDTO>
    },

    // Get report details
    getReportDetails: async (reportId) => {
        const response = await axios.get(`/library/reports/${reportId}`)
        return response.data // LibraryReportResponseDTO
    },

    // Download report file
    downloadReport: async (reportId) => {
        const response = await axios.get(`/library/reports/${reportId}/download`, {
            responseType: 'blob'
        })
        return response
    },

    // Rate a report
    rateReport: async (reportId, rating) => {
        const response = await axios.post(`/library/reports/${reportId}/rate`, null, {
            params: { rating }
        })
        return response.data
    },

    // Get user's rating for a report
    getUserRating: async (reportId) => {
        const response = await axios.get(`/library/reports/${reportId}/my-rating`)
        return response.data // Integer
    }
}