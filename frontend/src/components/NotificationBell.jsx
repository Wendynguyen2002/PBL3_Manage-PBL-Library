import { useState, useEffect, useRef } from 'react'
import { notificationAPI } from '../api/notificationAPI'
import NotificationModal from './NotificationModal'

export default function NotificationBell() {
    const [showDropdown, setShowDropdown] = useState(false)
    const [showModal, setShowModal] = useState(false)
    const [unreadCount, setUnreadCount] = useState(0)
    const [unreadNotifications, setUnreadNotifications] = useState([])
    const [loading, setLoading] = useState(false)
    const dropdownRef = useRef(null)

    useEffect(() => {
        fetchUnreadCount()
        const interval = setInterval(fetchUnreadCount, 30000)
        return () => clearInterval(interval)
    }, [])

    const fetchUnreadCount = async () => {
        try {
            const count = await notificationAPI.getUnreadCount()
            setUnreadCount(count)
        } catch (error) {
            console.error('Failed to fetch unread count:', error)
        }
    }

    const fetchUnreadNotifications = async () => {
        setLoading(true)
        try {
            const notifications = await notificationAPI.getUnreadNotifications()
            setUnreadNotifications(notifications)
        } catch (error) {
            console.error('Failed to fetch unread notifications:', error)
        } finally {
            setLoading(false)
        }
    }

    const handleBellClick = async () => {
        if (!showDropdown) {
            await fetchUnreadNotifications()
        }
        setShowDropdown(!showDropdown)
    }

    const handleMarkAllAsRead = async () => {
        try {
            await notificationAPI.markAllAsRead()
            setUnreadCount(0)
            setUnreadNotifications([])
            setShowDropdown(false)
        } catch (error) {
            console.error('Failed to mark all as read:', error)
        }
    }

    const handleSeeAll = () => {
        setShowDropdown(false)
        setShowModal(true)
    }

    useEffect(() => {
        function handleClickOutside(event) {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
                setShowDropdown(false)
            }
        }
        document.addEventListener('mousedown', handleClickOutside)
        return () => document.removeEventListener('mousedown', handleClickOutside)
    }, [])

    const formatTime = (dateTime) => {
        const date = new Date(dateTime)
        const now = new Date()
        const diffMs = now - date
        const diffMins = Math.floor(diffMs / 60000)
        const diffHours = Math.floor(diffMs / 3600000)
        const diffDays = Math.floor(diffMs / 86400000)

        if (diffMins < 1) return 'Just now'
        if (diffMins < 60) return `${diffMins} min ago`
        if (diffHours < 24) return `${diffHours} hour${diffHours > 1 ? 's' : ''} ago`
        return `${diffDays} day${diffDays > 1 ? 's' : ''} ago`
    }

    return (
        <>
            <div className="relative" ref={dropdownRef}>
                <button
                    onClick={handleBellClick}
                    className="relative p-2 text-white hover:text-blue-200 rounded-lg hover:bg-blue-500 transition-colors"
                    title="Notifications"
                >
                    <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
                    </svg>
                    {unreadCount > 0 && (
                        <span className="absolute top-0 right-0 inline-flex items-center justify-center px-1.5 py-0.5 text-xs font-bold leading-none text-white transform translate-x-1/2 -translate-y-1/2 bg-red-600 rounded-full">
              {unreadCount > 99 ? '99+' : unreadCount}
            </span>
                    )}
                </button>

                {/* Dropdown */}
                {showDropdown && (
                    <div className="absolute right-0 mt-2 w-80 bg-white rounded-lg shadow-lg border border-gray-200 z-50 overflow-hidden">
                        <div className="p-3 border-b border-gray-200">
                            <div className="flex justify-between items-center">
                                <h3 className="font-semibold text-gray-900">Notifications</h3>
                                <div className="flex space-x-3">
                                    <button
                                        onClick={handleSeeAll}
                                        className="text-xs text-blue-600 hover:text-blue-700"
                                        title="See all notifications"
                                    >
                                        See all
                                    </button>
                                    <button
                                        onClick={handleMarkAllAsRead}
                                        className="text-xs text-gray-600 hover:text-gray-700"
                                        title="Mark all as read"
                                    >
                                        Mark all as read
                                    </button>
                                </div>
                            </div>
                        </div>
                        <div className="max-h-96 overflow-y-auto">
                            {loading ? (
                                <div className="p-4 text-center">
                                    <div className="inline-block animate-spin rounded-full h-5 w-5 border-b-2 border-blue-600"></div>
                                </div>
                            ) : unreadNotifications.length === 0 ? (
                                <p className="p-4 text-center text-gray-500">
                                    No unread notifications
                                </p>
                            ) : (
                                unreadNotifications.map((notif) => (
                                    <div
                                        key={notif.id}
                                        className="p-3 hover:bg-gray-50 border-b border-gray-100 cursor-pointer transition-colors"
                                    >
                                        <p className="text-sm font-medium text-gray-900">{notif.title}</p>
                                        <p className="text-xs text-gray-600 mt-0.5">{notif.message}</p>
                                        <p className="text-xs text-gray-400 mt-1">{formatTime(notif.createdAt)}</p>
                                    </div>
                                ))
                            )}
                        </div>
                    </div>
                )}
            </div>

            {/* Modal - rendered outside the relative container */}
            <NotificationModal
                isOpen={showModal}
                onClose={() => setShowModal(false)}
                onUnreadCountChange={fetchUnreadCount}
            />
        </>
    )
}