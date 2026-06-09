import { useState, useRef, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../hooks/useAuth'
import NotificationBell from '../NotificationBell'

export default function Navbar() {
    const { user, logout } = useAuth()
    const navigate = useNavigate()
    const [showAccountMenu, setShowAccountMenu] = useState(false)
    const menuRef = useRef(null)

    useEffect(() => {
        function handleClickOutside(event) {
            if (menuRef.current && !menuRef.current.contains(event.target)) {
                setShowAccountMenu(false)
            }
        }
        document.addEventListener('mousedown', handleClickOutside)
        return () => document.removeEventListener('mousedown', handleClickOutside)
    }, [])

    const handleLogout = async () => {
        await logout()
        navigate('/')  // Go to Welcome page, not Login
    }

    return (
        <nav className="bg-blue-300 shadow-sm">
            <div className="px-4 sm:px-6 lg:px-8">
                <div className="flex justify-between h-16">
                    <div className="flex items-center">
            <span className="text-lg font-semibold text-white">
              PBL Manager
            </span>
                    </div>

                    <div className="flex items-center space-x-4">
                        {/* Notification Bell Component */}
                        <NotificationBell />

                        {/* Account Icon */}
                        <div className="relative" ref={menuRef}>
                            <button
                                onClick={() => setShowAccountMenu(!showAccountMenu)}
                                className="flex items-center space-x-2 p-2 rounded-lg hover:bg-blue-500"
                            >
                                <div className="w-8 h-8 rounded-full bg-blue-500 flex items-center justify-center text-white font-semibold">
                                    {user?.name?.charAt(0)?.toUpperCase() || user?.email?.charAt(0)?.toUpperCase() || 'U'}
                                </div>
                            </button>

                            {showAccountMenu && (
                                <div className="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg border border-gray-200 z-50">
                                    <div className="px-4 py-2 border-b border-gray-200">
                                        <p className="text-sm font-medium text-gray-900 truncate">
                                            {user?.name || user?.email}
                                        </p>
                                        <p className="text-xs text-gray-500 capitalize">
                                            {user?.role?.toLowerCase()}
                                        </p>
                                    </div>
                                    <button
                                        onClick={() => {
                                            setShowAccountMenu(false)
                                            navigate('/profile')
                                        }}
                                        className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                                    >
                                        Personal information
                                    </button>
                                    <button
                                        onClick={handleLogout}
                                        className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-gray-100 rounded-b-lg"
                                    >
                                        Logout
                                    </button>
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </nav>
    )
}