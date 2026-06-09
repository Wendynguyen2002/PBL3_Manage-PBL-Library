import axios from './axiosConfig'

export const notificationAPI = {
    // Get all notifications (paginated)
    getAllNotifications: async (page = 0, size = 20) => {
        const response = await axios.get('/notifications', { params: { page, size } })
        return response.data // List<NotificationResponseDTO>
    },

    // Get total count of all notifications
    getTotalCount: async () => {
        const response = await axios.get('/notifications/count')
        return response.data // Long
    },

    // Get unread notifications (for bell dropdown)
    getUnreadNotifications: async () => {
        const response = await axios.get('/notifications/unread')
        return response.data // List<NotificationResponseDTO>
    },

    // Get unread count (for badge)
    getUnreadCount: async () => {
        const response = await axios.get('/notifications/unread/count')
        return response.data // Long
    },

    // Mark all as read
    markAllAsRead: async () => {
        const response = await axios.put('/notifications/mark-all-as-read')
        return response.data
    },

    // Delete multiple notifications by IDs
    deleteNotifications: async (notificationIds) => {
        const response = await axios.delete('/notifications', { data: notificationIds })
        return response.data
    },

    // Delete all read notifications
    deleteAllRead: async () => {
        const response = await axios.delete('/notifications/read')
        return response.data
    },
}