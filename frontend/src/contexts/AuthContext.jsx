import { createContext, useState, useEffect } from 'react'
import { authAPI } from '../api/authAPI'

export const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)
  const [token, setToken] = useState(localStorage.getItem('token'))

  useEffect(() => {
    if (token) {
      fetchUser()
    } else {
      setLoading(false)
    }
  }, [token])

  const fetchUser = async () => {
    try {
      const userData = await authAPI.getMe()
      // Convert from backend response: { userId, email, role, fullName }
      setUser({
        id: userData.userId,
        email: userData.email,
        role: userData.role,
        name: userData.fullName,
      })
    } catch (error) {
      console.error('Failed to fetch user:', error)
      logout()
    } finally {
      setLoading(false)
    }
  }

  const login = async (email, password) => {
    const response = await authAPI.login(email, password)
    const { token: newToken, id, email: userEmail, role } = response
    localStorage.setItem('token', newToken)
    setToken(newToken)
    setUser({
      id,
      email: userEmail,
      role,
      name: userEmail.split('@')[0], // Temporary until fullName is set
    })
    return response
  }

  const logout = async () => {
    try {
      await authAPI.logout()
    } catch (error) {
      console.error('Logout API error:', error)
    } finally {
      localStorage.removeItem('token')
      setToken(null)
      setUser(null)
    }
  }

  const value = {
    user,
    setUser,
    login,
    logout,
    isAuthenticated: !!token,
    loading,
    token,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}