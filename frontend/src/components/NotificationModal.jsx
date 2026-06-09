import { useState, useEffect } from 'react'
import { notificationAPI } from '../api/notificationAPI'

export default function NotificationModal({ isOpen, onClose, onUnreadCountChange }) {
    const [notifications, setNotifications] = useState([])
    const [totalCount, setTotalCount] = useState(0)
    const [loading, setLoading] = useState(true)
    const [selectedIds, setSelectedIds] = useState(new Set())
    const [isDeleteMode, setIsDeleteMode] = useState(false)

    useEffect(() => {
        if (isOpen) {
            fetchNotifications()
            fetchTotalCount()
            // Prevent body scroll when modal is open
            document.body.style.overflow = 'hidden'
        } else {
            document.body.style.overflow = 'unset'
            setSelectedIds(new Set())
            setIsDeleteMode(false)
        }

        return () => {
            document.body.style.overflow = 'unset'
        }
    }, [isOpen])

    const fetchNotifications = async () => {
        setLoading(true)
        try {
            const data = await notificationAPI.getAllNotifications(0, 50)
            setNotifications(data)
        } catch (error) {
            console.error('Failed to fetch notifications:', error)
        } finally {
            setLoading(false)
        }
    }

    const fetchTotalCount = async () => {
        try {
            const count = await notificationAPI.getTotalCount()
            setTotalCount(count)
        } catch (error) {
            console.error('Failed to fetch total count:', error)
        }
    }

    const handleDeleteAllRead = async () => {
        if (window.confirm('Are you sure you want to delete all read notifications?')) {
            try {
                await notificationAPI.deleteAllRead()
                await fetchNotifications()
                await fetchTotalCount()
                if (onUnreadCountChange) onUnreadCountChange()
            } catch (error) {
                console.error('Failed to delete read notifications:', error)
            }
        }
    }

    const handleDeleteSelected = async () => {
        if (selectedIds.size === 0) return
        const confirmMsg = `Are you sure you want to delete ${selectedIds.size} notification(s)?`
        if (window.confirm(confirmMsg)) {
            try {
                await notificationAPI.deleteNotifications(Array.from(selectedIds))
                setSelectedIds(new Set())
                setIsDeleteMode(false)
                await fetchNotifications()
                await fetchTotalCount()
                if (onUnreadCountChange) onUnreadCountChange()
            } catch (error) {
                console.error('Failed to delete notifications:', error)
            }
        }
    }

    const handleSelectAll = () => {
        if (selectedIds.size === notifications.length) {
            setSelectedIds(new Set())
        } else {
            setSelectedIds(new Set(notifications.map(n => n.id)))
        }
    }

    const handleToggleSelect = (id) => {
        const newSelected = new Set(selectedIds)
        if (newSelected.has(id)) {
            newSelected.delete(id)
        } else {
            newSelected.add(id)
        }
        setSelectedIds(newSelected)
    }

    const formatDate = (dateTime) => {
        const date = new Date(dateTime)
        return date.toLocaleDateString() + ' ' + date.toLocaleTimeString()
    }

    // Handle ESC key to close modal
    useEffect(() => {
        const handleEsc = (event) => {
            if (event.key === 'Escape') {
                onClose()
            }
        }
        if (isOpen) {
            document.addEventListener('keydown', handleEsc)
        }
        return () => document.removeEventListener('keydown', handleEsc)
    }, [isOpen, onClose])

    if (!isOpen) return null

    return (
        <div className="fixed inset-0 z-5 flex items-center justify-center">
            {/* Backdrop */}
            <div
                className="absolute inset-0 bg-black/50"
                onClick={onClose}
            ></div>

            {/* Modal Panel */}
            <div className="relative bg-white rounded-lg shadow-xl w-full max-w-md max-h-[80vh] flex flex-col mx-4">
                {/* Header */}
                <div className="px-4 py-3 border-b border-gray-200 flex justify-between items-center bg-gray-50 rounded-t-lg">
                    <div className="flex items-center space-x-3">
                        <h3 className="text-lg font-medium text-gray-900">
                            All Notifications
                        </h3>
                        <span className="px-2 py-0.5 text-xs rounded-full bg-gray-200 text-gray-700">
              {totalCount}
            </span>
                    </div>
                    <div className="flex items-center space-x-2">
                        {/* Trash icon - Delete all read */}
                        <button
                            onClick={handleDeleteAllRead}
                            className="p-1.5 text-gray-500 hover:text-red-600 rounded-lg hover:bg-gray-100"
                            title="Delete all read notifications"
                        >
                            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                            </svg>
                        </button>

                        {/* Mop icon - Delete mode */}
                        <button
                            onClick={() => {
                                setIsDeleteMode(!isDeleteMode)
                                setSelectedIds(new Set())
                            }}
                            className={`p-1.5 rounded-lg transition-colors ${
                                isDeleteMode
                                    ? 'text-red-600 bg-red-50'
                                    : 'text-gray-500 hover:text-red-600 hover:bg-gray-100'
                            }`}
                            title={isDeleteMode ? 'Exit delete mode' : 'Delete notifications'}
                        >
                            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 12h.01M12 12h.01M19 12h.01M6 12a1 1 0 11-2 0 1 1 0 012 0zm7 0a1 1 0 11-2 0 1 1 0 012 0zm7 0a1 1 0 11-2 0 1 1 0 012 0z" />
                            </svg>
                        </button>

                        {/* Delete button (visible in delete mode when items selected) */}
                        {isDeleteMode && selectedIds.size > 0 && (
                            <button
                                onClick={handleDeleteSelected}
                                className="px-3 py-1 text-sm bg-red-600 hover:bg-red-700 text-white rounded-md transition-colors"
                            >
                                Delete ({selectedIds.size})
                            </button>
                        )}

                        {/* Select All button (in delete mode) */}
                        {isDeleteMode && notifications.length > 0 && (
                            <button
                                onClick={handleSelectAll}
                                className="text-xs text-blue-600 hover:text-blue-700"
                            >
                                {selectedIds.size === notifications.length ? 'Deselect All' : 'Select All'}
                            </button>
                        )}

                        {/* Close button */}
                        <button
                            onClick={onClose}
                            className="p-1.5 text-gray-500 hover:text-gray-700 rounded-lg hover:bg-gray-100"
                            title="Close"
                        >
                            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                            </svg>
                        </button>
                    </div>
                </div>

                {/* Body */}
                <div className="flex-1 overflow-y-auto p-0">
                    {loading ? (
                        <div className="flex justify-center items-center py-12">
                            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                        </div>
                    ) : notifications.length === 0 ? (
                        <p className="text-center text-gray-500 py-12">
                            You don't have any notifications at the moment
                        </p>
                    ) : (
                        <div className="divide-y divide-gray-100">
                            {notifications.map((notif) => (
                                <div
                                    key={notif.id}
                                    className={`p-4 hover:bg-gray-50 transition-colors ${
                                        !notif.read ? 'bg-blue-50' : ''
                                    }`}
                                >
                                    <div className="flex items-start space-x-3">
                                        {/* Checkbox (visible in delete mode) */}
                                        {isDeleteMode && (
                                            <input
                                                type="checkbox"
                                                checked={selectedIds.has(notif.id)}
                                                onChange={() => handleToggleSelect(notif.id)}
                                                className="mt-1 w-4 h-4 text-blue-600 rounded border-gray-300 focus:ring-blue-500"
                                            />
                                        )}
                                        <div className="flex-1">
                                            <div className="flex items-center justify-between">
                                                <p className="text-sm font-medium text-gray-900">
                                                    {notif.title}
                                                    {!notif.read && (
                                                        <span className="ml-2 inline-block w-2 h-2 bg-blue-600 rounded-full"></span>
                                                    )}
                                                </p>
                                                <span className="text-xs text-gray-400">
                          {formatDate(notif.createdAt)}
                        </span>
                                            </div>
                                            <p className="text-sm text-gray-600 mt-1">
                                                {notif.message}
                                            </p>
                                            {notif.type && (
                                                <span className="inline-block mt-2 text-xs px-2 py-0.5 rounded-full bg-gray-100 text-gray-600">
                          {notif.type}
                        </span>
                                            )}
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>

                {/* Footer */}
                <div className="px-4 py-3 border-t border-gray-200 bg-gray-50 rounded-b-lg">
                    <div className="flex justify-between text-xs text-gray-500">
                        <span>Total: {totalCount} notifications</span>
                        <span>Showing latest {notifications.length}</span>
                    </div>
                </div>
            </div>
        </div>
    )
}