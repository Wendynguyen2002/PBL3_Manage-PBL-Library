import axios from './axiosConfig'

export const authAPI = {
  login: async (email, password) => {
    const response = await axios.post('/auth/login', { email, password })
    // Response: { id, email, role, token, message }
    return response.data
  },

  logout: async () => {
    const response = await axios.post('/auth/logout')
    return response.data
  },

  getMe: async () => {
    const response = await axios.get('/auth/me')
    // Response: { userId, email, role, fullName }
    return response.data
  },
}