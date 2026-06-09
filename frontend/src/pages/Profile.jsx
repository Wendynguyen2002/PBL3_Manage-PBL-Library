import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'
import Sidebar from '../components/Layout/Sidebar'
import Navbar from '../components/Layout/Navbar'
import Modal from '../components/common/Modal'
import ErrorAlert from '../components/common/ErrorAlert'
import { adminAPI } from '../api/adminAPI'
import { studentAPI } from '../api/studentAPI'
import { lecturerAPI } from '../api/lecturerAPI'

export default function Profile() {
    const { user } = useAuth()
    const navigate = useNavigate()
    const [profile, setProfile] = useState(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [passwordModal, setPasswordModal] = useState(false)
    const [passwordData, setPasswordData] = useState({ currentPassword: '', newPassword: '' })
    const [updatingPassword, setUpdatingPassword] = useState(false)

    // Edit profile state
    const [editModal, setEditModal] = useState(false)
    const [editFormData, setEditFormData] = useState({})
    const [updatingProfile, setUpdatingProfile] = useState(false)

    const role = user?.role?.toUpperCase()

    useEffect(() => {
        fetchProfile()
    }, [role])

    const fetchProfile = async () => {
        setLoading(true)
        try {
            let data
            if (role === 'ADMIN') {
                data = await adminAPI.getProfile()
            } else if (role === 'STUDENT') {
                data = await studentAPI.getOwnProfile()
            } else if (role === 'LECTURER') {
                data = await lecturerAPI.getOwnProfile()
            }
            setProfile(data)
            // Initialize edit form data
            if (role === 'STUDENT') {
                setEditFormData({
                    phoneNumber: data.phoneNumber || '',
                    homeTown: data.homeTown || ''
                })
            } else if (role === 'LECTURER') {
                setEditFormData({
                    phoneNumber: data.phoneNumber || '',
                    homeTown: data.homeTown || '',
                    specialization: data.specialization || '',
                    position: data.position || '',
                    degree: data.degree || ''
                })
            }
        } catch (err) {
            setError('Failed to load profile')
        } finally {
            setLoading(false)
        }
    }

    const handleChangePassword = async (e) => {
        e.preventDefault()
        setUpdatingPassword(true)
        try {
            if (role === 'ADMIN') {
                await adminAPI.changePassword(passwordData)
            } else if (role === 'STUDENT') {
                await studentAPI.changePassword(passwordData)
            } else if (role === 'LECTURER') {
                await lecturerAPI.changePassword(passwordData)
            }
            alert('Password changed successfully!')
            setPasswordModal(false)
            setPasswordData({ currentPassword: '', newPassword: '' })
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to change password')
        } finally {
            setUpdatingPassword(false)
        }
    }

    const handleUpdateProfile = async (e) => {
        e.preventDefault()
        setUpdatingProfile(true)
        try {
            if (role === 'STUDENT') {
                await studentAPI.updateOwnProfile(editFormData)
            } else if (role === 'LECTURER') {
                await lecturerAPI.updateOwnProfile(editFormData)
            }
            alert('Profile updated successfully!')
            setEditModal(false)
            await fetchProfile() // Refresh profile data
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to update profile')
        } finally {
            setUpdatingProfile(false)
        }
    }

    const handleEditFieldChange = (field, value) => {
        setEditFormData({ ...editFormData, [field]: value })
    }

    if (loading) {
        return (
            <div className="flex h-screen bg-gray-50">
                <Sidebar />
                <div className="flex-1 flex items-center justify-center">
                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
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
                    <div className="max-w-3xl mx-auto">
                        {/* Back Arrow */}
                        <button
                            onClick={() => navigate('/dashboard')}
                            className="mb-4 flex items-center text-blue-600 hover:text-blue-700"
                        >
                            <svg className="w-5 h-5 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
                            </svg>
                            Back to Dashboard
                        </button>

                        <h1 className="text-2xl font-bold text-gray-900 mb-6">
                            Personal Information
                        </h1>

                        {error && <ErrorAlert message={error} onClose={() => setError('')} />}

                        <div className="bg-white rounded-lg shadow overflow-hidden">
                            <div className="p-6 space-y-4">
                                <div className="grid grid-cols-2 gap-4">
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700">ID</label>
                                        <p className="mt-1 text-gray-900">{profile?.id}</p>
                                    </div>
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700">Full Name</label>
                                        <p className="mt-1 text-gray-900">{profile?.fullName}</p>
                                    </div>
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700">Email</label>
                                        <p className="mt-1 text-gray-900">{profile?.email}</p>
                                    </div>
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700">Role</label>
                                        <p className="mt-1 text-gray-900 capitalize">{profile?.role?.toLowerCase()}</p>
                                    </div>
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700">Gender</label>
                                        <p className="mt-1 text-gray-900">{profile?.gender || 'Not provided'}</p>
                                    </div>
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700">Date of Birth</label>
                                        <p className="mt-1 text-gray-900">
                                            {profile?.dateOfBirth ? new Date(profile.dateOfBirth).toLocaleDateString() : 'Not provided'}
                                        </p>
                                    </div>
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700">Phone Number</label>
                                        <p className="mt-1 text-gray-900">{profile?.phoneNumber || 'Not provided'}</p>
                                    </div>
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700">Home Town</label>
                                        <p className="mt-1 text-gray-900">{profile?.homeTown || 'Not provided'}</p>
                                    </div>

                                    {role === 'STUDENT' && (
                                        <>
                                            <div>
                                                <label className="block text-sm font-medium text-gray-700">Home Class</label>
                                                <p className="mt-1 text-gray-900">{profile?.homeClass || 'Not provided'}</p>
                                            </div>
                                            <div>
                                                <label className="block text-sm font-medium text-gray-700">Major</label>
                                                <p className="mt-1 text-gray-900">{profile?.majorName || 'Not provided'}</p>
                                            </div>
                                        </>
                                    )}

                                    {role === 'LECTURER' && (
                                        <>
                                            <div>
                                                <label className="block text-sm font-medium text-gray-700">Specialization</label>
                                                <p className="mt-1 text-gray-900">{profile?.specialization || 'Not provided'}</p>
                                            </div>
                                            <div>
                                                <label className="block text-sm font-medium text-gray-700">Position</label>
                                                <p className="mt-1 text-gray-900">{profile?.position || 'Not provided'}</p>
                                            </div>
                                            <div>
                                                <label className="block text-sm font-medium text-gray-700">Degree</label>
                                                <p className="mt-1 text-gray-900">{profile?.degree || 'Not provided'}</p>
                                            </div>
                                            <div>
                                                <label className="block text-sm font-medium text-gray-700">Department</label>
                                                <p className="mt-1 text-gray-900">{profile?.departmentName || 'Not provided'}</p>
                                            </div>
                                        </>
                                    )}
                                </div>

                                <div className="pt-4 flex space-x-3">
                                    {/* Edit Profile Button - Only for Student and Lecturer */}
                                    {role !== 'ADMIN' && (
                                        <button
                                            onClick={() => setEditModal(true)}
                                            className="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700"
                                        >
                                            Edit Profile
                                        </button>
                                    )}

                                    {/* Change Password Button - All roles */}
                                    <button
                                        onClick={() => setPasswordModal(true)}
                                        className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
                                    >
                                        Change Password
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </main>
            </div>

            {/* Edit Profile Modal */}
            <Modal
                isOpen={editModal}
                onClose={() => {
                    setEditModal(false)
                    // Reset form data
                    if (role === 'STUDENT') {
                        setEditFormData({
                            phoneNumber: profile?.phoneNumber || '',
                            homeTown: profile?.homeTown || ''
                        })
                    } else if (role === 'LECTURER') {
                        setEditFormData({
                            phoneNumber: profile?.phoneNumber || '',
                            homeTown: profile?.homeTown || '',
                            specialization: profile?.specialization || '',
                            position: profile?.position || '',
                            degree: profile?.degree || ''
                        })
                    }
                }}
                title="Edit Profile"
                size="lg"
            >
                <form onSubmit={handleUpdateProfile} className="space-y-4">
                    {role === 'STUDENT' && (
                        <>
                            <div>
                                <label className="block text-sm font-medium text-gray-700">Phone Number</label>
                                <input
                                    type="text"
                                    value={editFormData.phoneNumber || ''}
                                    onChange={(e) => handleEditFieldChange('phoneNumber', e.target.value)}
                                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                                    placeholder="+84123456789"
                                />
                                <p className="mt-1 text-xs text-gray-500">Format: +84123456789 or 0123456789</p>
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700">Home Town</label>
                                <input
                                    type="text"
                                    value={editFormData.homeTown || ''}
                                    onChange={(e) => handleEditFieldChange('homeTown', e.target.value)}
                                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                                    placeholder="Da Nang, Vietnam"
                                />
                            </div>
                        </>
                    )}

                    {role === 'LECTURER' && (
                        <>
                            <div>
                                <label className="block text-sm font-medium text-gray-700">Phone Number</label>
                                <input
                                    type="text"
                                    value={editFormData.phoneNumber || ''}
                                    onChange={(e) => handleEditFieldChange('phoneNumber', e.target.value)}
                                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                                    placeholder="+84123456789"
                                />
                                <p className="mt-1 text-xs text-gray-500">Format: +84123456789 or 0123456789</p>
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700">Home Town</label>
                                <input
                                    type="text"
                                    value={editFormData.homeTown || ''}
                                    onChange={(e) => handleEditFieldChange('homeTown', e.target.value)}
                                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                                    placeholder="Da Nang, Vietnam"
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700">Specialization</label>
                                <input
                                    type="text"
                                    value={editFormData.specialization || ''}
                                    onChange={(e) => handleEditFieldChange('specialization', e.target.value)}
                                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                                    placeholder="Software Engineering"
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700">Position</label>
                                <input
                                    type="text"
                                    value={editFormData.position || ''}
                                    onChange={(e) => handleEditFieldChange('position', e.target.value)}
                                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                                    placeholder="Senior Lecturer"
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700">Degree</label>
                                <input
                                    type="text"
                                    value={editFormData.degree || ''}
                                    onChange={(e) => handleEditFieldChange('degree', e.target.value)}
                                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                                    placeholder="PhD in Computer Science"
                                />
                            </div>
                        </>
                    )}

                    <div className="flex justify-end space-x-3 pt-4">
                        <button
                            type="button"
                            onClick={() => setEditModal(false)}
                            className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            disabled={updatingProfile}
                            className="px-4 py-2 text-sm font-medium text-white bg-green-600 rounded-md hover:bg-green-700 disabled:opacity-50"
                        >
                            {updatingProfile ? 'Saving...' : 'Save Changes'}
                        </button>
                    </div>
                </form>
            </Modal>

            {/* Change Password Modal */}
            <Modal
                isOpen={passwordModal}
                onClose={() => {
                    setPasswordModal(false)
                    setPasswordData({ currentPassword: '', newPassword: '' })
                }}
                title="Change Password"
            >
                <form onSubmit={handleChangePassword} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Current Password</label>
                        <input
                            type="password"
                            required
                            value={passwordData.currentPassword}
                            onChange={(e) => setPasswordData({ ...passwordData, currentPassword: e.target.value })}
                            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">New Password</label>
                        <input
                            type="password"
                            required
                            value={passwordData.newPassword}
                            onChange={(e) => setPasswordData({ ...passwordData, newPassword: e.target.value })}
                            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                        />
                        <p className="mt-1 text-xs text-gray-500">
                            Password must contain at least 8 characters, one digit, one lowercase, one uppercase, and one special character
                        </p>
                    </div>
                    <div className="flex justify-end space-x-3">
                        <button
                            type="button"
                            onClick={() => {
                                setPasswordModal(false)
                                setPasswordData({ currentPassword: '', newPassword: '' })
                            }}
                            className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            disabled={updatingPassword}
                            className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 disabled:opacity-50"
                        >
                            {updatingPassword ? 'Changing...' : 'Change Password'}
                        </button>
                    </div>
                </form>
            </Modal>
        </div>
    )
}