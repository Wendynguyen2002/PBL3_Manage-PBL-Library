import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'
import Sidebar from '../components/Layout/Sidebar'
import Navbar from '../components/Layout/Navbar'
import LoadingSpinner from '../components/common/LoadingSpinner'
import ErrorAlert from '../components/common/ErrorAlert'
import ConfirmDialog from '../components/common/ConfirmDialog'
import Modal from '../components/common/Modal'
import { pblClassAPI } from '../api/pblClassAPI'
import { majorAPI } from '../api/majorAPI'
import { departmentAPI } from '../api/departmentAPI'

export default function Dashboard() {
    const { user } = useAuth()
    const navigate = useNavigate()
    const role = user?.role?.toUpperCase()
    const isLecturer = role === 'LECTURER'
    const isStudent = role === 'STUDENT'
    const isAdmin = role === 'ADMIN'

    const [pblClasses, setPblClasses] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    // Create class modal state
    const [showCreateModal, setShowCreateModal] = useState(false)
    const [creating, setCreating] = useState(false)
    const [deleteConfirm, setDeleteConfirm] = useState(null)
    const [allMajors, setAllMajors] = useState([])

    // Form data for creating class
    const [formData, setFormData] = useState({
        id: '',
        className: '',
        semester: '',
        maxStudentsPerGroup: 3,
        majorId: [],
        finalReportDeadline: ''
    })

    useEffect(() => {
        if (!isAdmin) {
            fetchMyClasses()
        } else {
            setLoading(false)
        }
    }, [isAdmin])

    const fetchMyClasses = async () => {
        setLoading(true)
        try {
            const data = await pblClassAPI.getMyPblClasses()
            setPblClasses(data)
        } catch (err) {
            setError('Failed to load your PBL classes')
            console.error(err)
        } finally {
            setLoading(false)
        }
    }

    const fetchAllMajors = async () => {
        try {
            const majors = await majorAPI.getAllMajorsList()
            setAllMajors(majors)
        } catch (err) {
            console.error('Failed to fetch majors:', err)
        }
    }

    const handleOpenCreateModal = async () => {
        await fetchAllMajors()  // Change this line
        setFormData({
            id: '',
            className: '',
            semester: '',
            maxStudentsPerGroup: 3,
            majorId: [],
            finalReportDeadline: ''
        })
        setShowCreateModal(true)
    }

    const handleMajorToggle = (majorId) => {
        setFormData(prev => ({
            ...prev,
            majorId: prev.majorId.includes(majorId)
                ? prev.majorId.filter(id => id !== majorId)
                : [...prev.majorId, majorId]
        }))
    }

    const handleCreateClass = async (e) => {
        e.preventDefault()
        if (formData.majorId.length === 0) {
            alert('Please select at least one major')
            return
        }
        setCreating(true)
        try {
            await pblClassAPI.createPblClass(formData)
            setShowCreateModal(false)
            await fetchMyClasses()
        } catch (err) {
            alert(err.response?.data?.message || 'Failed to create class')
        } finally {
            setCreating(false)
        }
    }

    const handleDeleteClass = async (classId) => {
        try {
            await pblClassAPI.deletePblClass(classId)
            setDeleteConfirm(null)
            await fetchMyClasses()
        } catch (err) {
            alert(err.response?.data?.message || 'Failed to delete class')
        }
    }

    const handleCardClick = (classId) => {
        navigate(`/pbl-class/${classId}`)
    }

    const adminCards = [
        { icon: SchoolIcon, title: 'School Management', description: 'Manage departments and majors', path: '/admin/school-management', color: 'bg-blue-500' },
        { icon: UsersIcon, title: 'Account Management', description: 'Manage students and lecturers', path: '/admin/account-management', color: 'bg-green-500' },
    ]

    if (loading) {
        return (
            <div className="flex h-screen bg-gray-50">
                <Sidebar />
                <div className="flex-1 flex flex-col">
                    <Navbar />
                    <div className="flex-1 flex items-center justify-center">
                        <LoadingSpinner />
                    </div>
                </div>
            </div>
        )
    }

    return (
        <div className="flex h-screen bg-gray-50">
            <Sidebar />
            <div className="flex-1 flex flex-col overflow-hidden">
                <Navbar />
                <main className="flex-1 overflow-y-auto p-6">
                    <div className="max-w-7xl mx-auto">
                        <h1 className="text-2xl font-bold text-gray-900 mb-2">
                            Welcome back, {user?.name || user?.email}!
                        </h1>
                        <p className="text-gray-600 mb-8">Here's an overview of your PBL Management System</p>

                        {/* Admin Cards - Always show for admin users */}
                        {isAdmin && (
                            <div className="mb-8">
                                <h2 className="text-xl font-semibold text-gray-900 mb-4">Administration</h2>
                                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                                    {adminCards.map((card, index) => (
                                        <div
                                            key={index}
                                            onClick={() => navigate(card.path)}
                                            className="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow cursor-pointer overflow-hidden"
                                        >
                                            <div className={`${card.color} p-4 flex justify-center`}>
                                                <card.icon />
                                            </div>
                                            <div className="p-6">
                                                <h3 className="text-lg font-semibold text-gray-900 mb-2">{card.title}</h3>
                                                <p className="text-gray-600">{card.description}</p>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            </div>
                        )}

                        {/* PBL Classes Section - for Lecturer and Student (not for Admin) */}
                        {!isAdmin && (
                            <div>
                                <div className="flex justify-between items-center mb-4">
                                    <h2 className="text-xl font-semibold text-gray-900">
                                        {isLecturer ? 'My PBL Classes' : 'My Enrolled Classes'}
                                    </h2>
                                    {isLecturer && (
                                        <button
                                            onClick={handleOpenCreateModal}
                                            className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 flex items-center space-x-2"
                                        >
                                            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                                            </svg>
                                            <span>Create New Class</span>
                                        </button>
                                    )}
                                </div>

                                {error && <ErrorAlert message={error} />}

                                {pblClasses.length === 0 ? (
                                    <div className="bg-white rounded-lg shadow p-12 text-center">
                                        <ClassesIcon className="w-16 h-16 text-gray-400 mx-auto mb-4" />
                                        <p className="text-gray-500 text-lg">
                                            {isLecturer
                                                ? "You haven't created any PBL classes yet."
                                                : "You're not enrolled in any PBL classes yet."}
                                        </p>
                                        {isLecturer && (
                                            <button
                                                onClick={handleOpenCreateModal}
                                                className="mt-4 px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
                                            >
                                                Create Your First Class
                                            </button>
                                        )}
                                    </div>
                                ) : (
                                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                                        {pblClasses.map((pblClass) => (
                                            <div
                                                key={pblClass.id}
                                                className="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow overflow-hidden group relative"
                                            >
                                                {/* Delete button - only visible to lecturer on hover */}
                                                {isLecturer && (
                                                    <button
                                                        onClick={(e) => {
                                                            e.stopPropagation()
                                                            setDeleteConfirm(pblClass)
                                                        }}
                                                        className="absolute top-2 right-2 p-2 bg-red-500 rounded-full opacity-0 group-hover:opacity-100 transition-opacity hover:bg-red-600 z-10"
                                                        title="Delete class"
                                                    >
                                                        <svg className="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                                        </svg>
                                                    </button>
                                                )}

                                                {/* Clickable card body */}
                                                <div
                                                    onClick={() => handleCardClick(pblClass.id)}
                                                    className="cursor-pointer"
                                                >
                                                    <div className="bg-gradient-to-r from-blue-500 to-blue-600 p-4 flex justify-center">
                                                        <ClassesIcon className="w-12 h-12 text-white" />
                                                    </div>
                                                    <div className="p-6">
                                                        <h3 className="text-lg font-semibold text-gray-900 mb-2">{pblClass.className}</h3>
                                                        <div className="space-y-1 text-sm text-gray-600">
                                                            <p>
                                                                <span className="font-medium">Semester:</span> {pblClass.semester}
                                                            </p>
                                                            <p>
                                                                <span className="font-medium">Lecturer:</span> {pblClass.lecturerName}
                                                            </p>
                                                            <p>
                                                                <span className="font-medium">Majors:</span> {pblClass.majorNames?.slice(0, 2).join(', ')}
                                                                {pblClass.majorNames?.length > 2 && ` +${pblClass.majorNames.length - 2}`}
                                                            </p>
                                                            {pblClass.finalReportDeadline && (
                                                                <p>
                                                                    <span className="font-medium">Deadline:</span>{' '}
                                                                    {new Date(pblClass.finalReportDeadline).toLocaleDateString()}
                                                                </p>
                                                            )}
                                                        </div>
                                                        {pblClass.finalReportLocked && (
                                                            <div className="mt-3">
                                                                <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-red-100 text-red-800">
                                                                    Report Locked
                                                                </span>
                                                            </div>
                                                        )}
                                                    </div>
                                                </div>
                                            </div>
                                        ))}
                                    </div>
                                )}
                            </div>
                        )}

                        {/* If admin has no other content to show, display a message */}
                        {isAdmin && (
                            <div className="text-center py-12 text-gray-500">
                                <p>Select an administration option above to get started.</p>
                            </div>
                        )}
                    </div>
                </main>
            </div>

            {/* Create Class Modal */}
            <Modal
                isOpen={showCreateModal}
                onClose={() => setShowCreateModal(false)}
                title="Create New PBL Class"
                size="lg"
            >
                <form onSubmit={handleCreateClass} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Class ID *</label>
                        <input
                            type="text"
                            required
                            placeholder="e.g., PBL2024_01"
                            value={formData.id}
                            onChange={(e) => setFormData({ ...formData, id: e.target.value })}
                            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                        />
                        <p className="mt-1 text-xs text-gray-500">Unique identifier for the class</p>
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700">Class Name *</label>
                        <input
                            type="text"
                            required
                            placeholder="e.g., Software Development Project"
                            value={formData.className}
                            onChange={(e) => setFormData({ ...formData, className: e.target.value })}
                            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700">Semester *</label>
                        <input
                            type="text"
                            required
                            placeholder="e.g., Spring 2024 or 2024-2025"
                            value={formData.semester}
                            onChange={(e) => setFormData({ ...formData, semester: e.target.value })}
                            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700">Max Students Per Group *</label>
                        <input
                            type="number"
                            required
                            min={1}
                            max={10}
                            value={formData.maxStudentsPerGroup}
                            onChange={(e) => setFormData({ ...formData, maxStudentsPerGroup: parseInt(e.target.value) })}
                            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Acceptable Majors * (Select at least one)
                        </label>
                        <div className="max-h-48 overflow-y-auto border border-gray-200 rounded-md p-3 space-y-2">
                            {allMajors.length === 0 ? (
                                <p className="text-gray-500 text-sm">Loading majors...</p>
                            ) : (
                                allMajors.map(major => (
                                    <label key={major.id} className="flex items-center space-x-2 cursor-pointer">
                                        <input
                                            type="checkbox"
                                            checked={formData.majorId.includes(major.id)}
                                            onChange={() => handleMajorToggle(major.id)}
                                            className="h-4 w-4 text-blue-600 rounded border-gray-300"
                                        />
                                        <span className="text-sm text-gray-700">{major.name}</span>
                                        {major.departmentName && (
                                            <span className="text-xs text-gray-400">({major.departmentName})</span>
                                        )}
                                    </label>
                                ))
                            )}
                        </div>
                        {formData.majorId.length > 0 && (
                            <p className="mt-1 text-xs text-green-600">{formData.majorId.length} major(s) selected</p>
                        )}
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700">Final Report Deadline *</label>
                        <input
                            type="datetime-local"
                            required
                            value={formData.finalReportDeadline}
                            onChange={(e) => setFormData({ ...formData, finalReportDeadline: e.target.value })}
                            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                        />
                    </div>

                    <div className="flex justify-end space-x-3 pt-4">
                        <button
                            type="button"
                            onClick={() => setShowCreateModal(false)}
                            className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            disabled={creating}
                            className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 disabled:opacity-50"
                        >
                            {creating ? 'Creating...' : 'Create Class'}
                        </button>
                    </div>
                </form>
            </Modal>

            {/* Delete Class Confirm Dialog */}
            <ConfirmDialog
                isOpen={!!deleteConfirm}
                onClose={() => setDeleteConfirm(null)}
                onConfirm={() => handleDeleteClass(deleteConfirm.id)}
                title="Delete Class"
                message={`Are you sure you want to delete "${deleteConfirm?.className}"? This will also delete all groups, projects, and submissions in this class. This action cannot be undone.`}
            />
        </div>
    )
}

// Icon components
function SchoolIcon() {
    return (
        <svg className="w-12 h-12 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 21H5a2 2 0 01-2-2V5a2 2 0 012-2h11l5 5v11a2 2 0 01-2 2z" />
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 21v-4H7v4" />
        </svg>
    )
}

function UsersIcon() {
    return (
        <svg className="w-12 h-12 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
        </svg>
    )
}

function ClassesIcon({ className = "w-12 h-12 text-white" }) {
    return (
        <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 21H5a2 2 0 01-2-2V5a2 2 0 012-2h11l5 5v11a2 2 0 01-2 2z" />
        </svg>
    )
}