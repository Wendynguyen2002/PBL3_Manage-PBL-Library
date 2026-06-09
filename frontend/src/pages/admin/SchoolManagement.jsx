import { useState, useEffect } from 'react'
import Sidebar from '../../components/Layout/Sidebar'
import Navbar from '../../components/Layout/Navbar'
import Modal from '../../components/common/Modal'
import ConfirmDialog from '../../components/common/ConfirmDialog'
import Pagination from '../../components/common/Pagination'
import ErrorAlert from '../../components/common/ErrorAlert'
import { departmentAPI } from '../../api/departmentAPI'
import { majorAPI } from '../../api/majorAPI'

export default function SchoolManagement() {
    const [activeTab, setActiveTab] = useState('departments')
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')

    // Department state
    const [departments, setDepartments] = useState([])
    const [departmentsTotalPages, setDepartmentsTotalPages] = useState(0)
    const [departmentsPage, setDepartmentsPage] = useState(0)
    const [departmentsSearch, setDepartmentsSearch] = useState('')
    const [departmentModal, setDepartmentModal] = useState({ open: false, mode: 'create', data: null })
    const [departmentToDelete, setDepartmentToDelete] = useState(null)
    const [departmentViewModal, setDepartmentViewModal] = useState({ open: false, data: null })
    const [viewDepartmentData, setViewDepartmentData] = useState(null)
    const [viewLoading, setViewLoading] = useState(false)

    // Major state
    const [majors, setMajors] = useState([])
    const [majorsTotalPages, setMajorsTotalPages] = useState(0)
    const [majorsPage, setMajorsPage] = useState(0)
    const [majorsSearch, setMajorsSearch] = useState('')
    const [majorModal, setMajorModal] = useState({ open: false, mode: 'create', data: null })
    const [majorToDelete, setMajorToDelete] = useState(null)
    const [departmentsDropdown, setDepartmentsDropdown] = useState([])

    useEffect(() => {
        if (activeTab === 'departments') {
            fetchDepartments()
        } else {
            fetchMajors()
            fetchDepartmentsDropdown()
        }
    }, [activeTab, departmentsPage, departmentsSearch, majorsPage, majorsSearch])

    const fetchDepartments = async () => {
        setLoading(true)
        try {
            const data = await departmentAPI.getAllDepartments(departmentsSearch, departmentsPage)
            setDepartments(data.content)
            setDepartmentsTotalPages(data.totalPages)
        } catch (err) {
            setError('Failed to fetch departments')
        } finally {
            setLoading(false)
        }
    }

    const fetchMajors = async () => {
        setLoading(true)
        try {
            const data = await majorAPI.getAllMajors(majorsSearch, majorsPage)
            setMajors(data.content)
            setMajorsTotalPages(data.totalPages)
        } catch (err) {
            setError('Failed to fetch majors')
        } finally {
            setLoading(false)
        }
    }

    const fetchDepartmentsDropdown = async () => {
        try {
            const data = await departmentAPI.getDepartmentsForDropdown()
            setDepartmentsDropdown(data)
        } catch (err) {
            console.error('Failed to fetch departments dropdown:', err)
        }
    }

    const handleViewDepartment = async (department) => {
        setViewLoading(true)
        setDepartmentViewModal({ open: true, data: department })
        try {
            const data = await departmentAPI.getDepartmentById(department.id)
            setViewDepartmentData(data)
        } catch (err) {
            setError('Failed to fetch department details')
        } finally {
            setViewLoading(false)
        }
    }

    const handleCreateDepartment = async (data) => {
        try {
            await departmentAPI.createDepartment(data)
            await fetchDepartments()
            setDepartmentModal({ open: false, mode: 'create', data: null })
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to create department')
        }
    }

    const handleUpdateDepartment = async (id, data) => {
        try {
            await departmentAPI.updateDepartment(id, data)
            await fetchDepartments()
            setDepartmentModal({ open: false, mode: 'create', data: null })
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to update department')
        }
    }

    const handleDeleteDepartment = async () => {
        try {
            await departmentAPI.deleteDepartment(departmentToDelete.id)
            await fetchDepartments()
            setDepartmentToDelete(null)
        } catch (err) {
            setError('Failed to delete department')
        }
    }

    const handleCreateMajor = async (data) => {
        try {
            await majorAPI.createMajor(data)
            await fetchMajors()
            setMajorModal({ open: false, mode: 'create', data: null })
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to create major')
        }
    }

    const handleUpdateMajor = async (id, data) => {
        try {
            await majorAPI.updateMajor(id, data)
            await fetchMajors()
            setMajorModal({ open: false, mode: 'create', data: null })
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to update major')
        }
    }

    const handleDeleteMajor = async () => {
        try {
            await majorAPI.deleteMajor(majorToDelete.id)
            await fetchMajors()
            setMajorToDelete(null)
        } catch (err) {
            setError('Failed to delete major')
        }
    }

    const DepartmentModalComponent = () => {
        const [formData, setFormData] = useState({ id: '', name: '' })

        useEffect(() => {
            if (departmentModal.data) {
                setFormData({ id: departmentModal.data.id, name: departmentModal.data.name })
            } else {
                setFormData({ id: '', name: '' })
            }
        }, [departmentModal.data])

        const handleSubmit = (e) => {
            e.preventDefault()
            if (departmentModal.mode === 'create') {
                handleCreateDepartment(formData)
            } else {
                handleUpdateDepartment(departmentModal.data.id, formData)
            }
        }

        return (
            <Modal
                isOpen={departmentModal.open}
                onClose={() => setDepartmentModal({ open: false, mode: 'create', data: null })}
                title={`${departmentModal.mode === 'create' ? 'Create' : 'Update'} Department`}
            >
                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Department ID</label>
                        <input
                            type="text"
                            required
                            disabled={departmentModal.mode === 'update'}
                            value={formData.id}
                            onChange={(e) => setFormData({ ...formData, id: e.target.value })}
                            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 disabled:bg-gray-100"
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Department Name</label>
                        <input
                            type="text"
                            required
                            value={formData.name}
                            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                        />
                    </div>
                    <div className="flex justify-end space-x-3">
                        <button
                            type="button"
                            onClick={() => setDepartmentModal({ open: false, mode: 'create', data: null })}
                            className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700"
                        >
                            {departmentModal.mode === 'create' ? 'Create' : 'Update'}
                        </button>
                    </div>
                </form>
            </Modal>
        )
    }

    const ViewDepartmentModal = () => {
        return (
            <Modal
                isOpen={departmentViewModal.open}
                onClose={() => {
                    setDepartmentViewModal({ open: false, data: null })
                    setViewDepartmentData(null)
                }}
                title={`Department Details: ${departmentViewModal.data?.name || ''}`}
                size="lg"
            >
                {viewLoading ? (
                    <div className="flex justify-center py-8">
                        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                    </div>
                ) : viewDepartmentData ? (
                    <div className="space-y-6">
                        {/* Basic Info */}
                        <div>
                            <h4 className="text-md font-semibold text-gray-900 mb-2">Basic Information</h4>
                            <div className="bg-gray-50 rounded-lg p-4 space-y-2">
                                <div>
                                    <span className="text-sm font-medium text-gray-500">Department ID:</span>
                                    <span className="ml-2 text-sm text-gray-900">{viewDepartmentData.id}</span>
                                </div>
                                <div>
                                    <span className="text-sm font-medium text-gray-500">Department Name:</span>
                                    <span className="ml-2 text-sm text-gray-900">{viewDepartmentData.name}</span>
                                </div>
                            </div>
                        </div>

                        {/* Majors List */}
                        <div>
                            <h4 className="text-md font-semibold text-gray-900 mb-2">
                                Majors ({viewDepartmentData.majorNames?.length || 0})
                            </h4>
                            <div className="bg-gray-50 rounded-lg p-4">
                                {viewDepartmentData.majorNames && viewDepartmentData.majorNames.length > 0 ? (
                                    <ul className="list-disc list-inside space-y-1">
                                        {viewDepartmentData.majorNames.map((major, index) => (
                                            <li key={index} className="text-sm text-gray-700">{major}</li>
                                        ))}
                                    </ul>
                                ) : (
                                    <p className="text-sm text-gray-500">No majors associated with this department</p>
                                )}
                            </div>
                        </div>

                        {/* Lecturers List */}
                        <div>
                            <h4 className="text-md font-semibold text-gray-900 mb-2">
                                Lecturers ({viewDepartmentData.lecturerNames?.length || 0})
                            </h4>
                            <div className="bg-gray-50 rounded-lg p-4">
                                {viewDepartmentData.lecturerNames && viewDepartmentData.lecturerNames.length > 0 ? (
                                    <ul className="list-disc list-inside space-y-1">
                                        {viewDepartmentData.lecturerNames.map((lecturer, index) => (
                                            <li key={index} className="text-sm text-gray-700">{lecturer}</li>
                                        ))}
                                    </ul>
                                ) : (
                                    <p className="text-sm text-gray-500">No lecturers associated with this department</p>
                                )}
                            </div>
                        </div>

                        <div className="flex justify-end">
                            <button
                                type="button"
                                onClick={() => {
                                    setDepartmentViewModal({ open: false, data: null })
                                    setViewDepartmentData(null)
                                }}
                                className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700"
                            >
                                Close
                            </button>
                        </div>
                    </div>
                ) : (
                    <p className="text-center text-gray-500 py-8">Failed to load department details</p>
                )}
            </Modal>
        )
    }

    const MajorModalComponent = () => {
        const [formData, setFormData] = useState({ id: '', name: '', departmentId: '' })
        const [localDepartments, setLocalDepartments] = useState([])

        // Fetch departments when modal opens
        useEffect(() => {
            if (majorModal.open) {
                const fetchDepartments = async () => {
                    try {
                        const data = await departmentAPI.getDepartmentsForDropdown()
                        setLocalDepartments(data)
                    } catch (error) {
                        console.error('Failed to fetch departments:', error)
                    }
                }
                fetchDepartments()
            }
        }, [majorModal.open])

        // Set form data when editing
        useEffect(() => {
            if (majorModal.mode === 'update' && majorModal.data) {
                setFormData({
                    id: majorModal.data.id,
                    name: majorModal.data.name,
                    departmentId: localDepartments.find(d => d.name === majorModal.data.departmentName)?.id || ''
                })
            } else if (majorModal.mode === 'create') {
                setFormData({ id: '', name: '', departmentId: '' })
            }
        }, [majorModal.data, majorModal.mode, localDepartments])

        const handleSubmit = (e) => {
            e.preventDefault()
            if (majorModal.mode === 'create') {
                handleCreateMajor(formData)
            } else {
                handleUpdateMajor(majorModal.data.id, formData)
            }
        }

        return (
            <Modal
                isOpen={majorModal.open}
                onClose={() => setMajorModal({ open: false, mode: 'create', data: null })}
                title={`${majorModal.mode === 'create' ? 'Create' : 'Update'} Major`}
                size="lg"
            >
                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Major ID</label>
                        <input
                            type="text"
                            required
                            disabled={majorModal.mode === 'update'}
                            value={formData.id}
                            onChange={(e) => setFormData({ ...formData, id: e.target.value })}
                            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 disabled:bg-gray-100"
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Major Name</label>
                        <input
                            type="text"
                            required
                            value={formData.name}
                            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Department</label>
                        <select
                            required
                            value={formData.departmentId}
                            onChange={(e) => setFormData({ ...formData, departmentId: e.target.value })}
                            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                        >
                            <option value="">Select Department</option>
                            {localDepartments.map(dept => (
                                <option key={dept.id} value={dept.id}>{dept.name}</option>
                            ))}
                        </select>
                    </div>
                    <div className="flex justify-end space-x-3">
                        <button
                            type="button"
                            onClick={() => setMajorModal({ open: false, mode: 'create', data: null })}
                            className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700"
                        >
                            {majorModal.mode === 'create' ? 'Create' : 'Update'}
                        </button>
                    </div>
                </form>
            </Modal>
        )
    }

    return (
        <div className="flex h-screen bg-gray-50">
            <Sidebar />
            <div className="flex-1 flex flex-col overflow-hidden">
                <Navbar />
                <main className="flex-1 overflow-y-auto p-6">
                    <div className="max-w-7xl mx-auto">
                        {error && <ErrorAlert message={error} onClose={() => setError('')} />}

                        <div className="border-b border-gray-200">
                            <nav className="-mb-px flex space-x-8">
                                <button
                                    onClick={() => setActiveTab('departments')}
                                    className={`py-2 px-1 border-b-2 font-medium text-sm ${
                                        activeTab === 'departments'
                                            ? 'border-blue-500 text-blue-600'
                                            : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                                    }`}
                                >
                                    Manage Departments
                                </button>
                                <button
                                    onClick={() => setActiveTab('majors')}
                                    className={`py-2 px-1 border-b-2 font-medium text-sm ${
                                        activeTab === 'majors'
                                            ? 'border-blue-500 text-blue-600'
                                            : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                                    }`}
                                >
                                    Manage Majors
                                </button>
                            </nav>
                        </div>

                        <div className="mt-6">
                            {activeTab === 'departments' ? (
                                <>
                                    <div className="flex justify-between items-center mb-4">
                                        <input
                                            type="text"
                                            placeholder="Search departments..."
                                            value={departmentsSearch}
                                            onChange={(e) => {
                                                setDepartmentsSearch(e.target.value)
                                                setDepartmentsPage(0)
                                            }}
                                            className="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500 w-64"
                                        />
                                        <button
                                            onClick={() => setDepartmentModal({ open: true, mode: 'create', data: null })}
                                            className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
                                        >
                                            Create Department
                                        </button>
                                    </div>

                                    <div className="bg-white shadow overflow-hidden sm:rounded-md">
                                        <table className="min-w-full divide-y divide-gray-200">
                                            <thead className="bg-gray-50">
                                            <tr>
                                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Name</th>
                                                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                                            </tr>
                                            </thead>
                                            <tbody className="bg-white divide-y divide-gray-200">
                                            {loading ? (
                                                <tr>
                                                    <td colSpan="3" className="px-6 py-4 text-center">Loading...</td>
                                                </tr>
                                            ) : departments.length === 0 ? (
                                                <tr>
                                                    <td colSpan="3" className="px-6 py-4 text-center text-gray-500">No departments found</td>
                                                </tr>
                                            ) : (
                                                departments.map((dept) => (
                                                    <tr key={dept.id}>
                                                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{dept.id}</td>
                                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{dept.name}</td>
                                                        <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                                            <button
                                                                onClick={() => handleViewDepartment(dept)}
                                                                className="text-blue-600 hover:text-blue-900 mr-3"
                                                                title="View Details"
                                                            >
                                                                ℹ️
                                                            </button>
                                                            <button
                                                                onClick={() => setDepartmentModal({ open: true, mode: 'update', data: dept })}
                                                                className="text-green-600 hover:text-green-900 mr-3"
                                                                title="Edit"
                                                            >
                                                                ✏️
                                                            </button>
                                                            <button
                                                                onClick={() => setDepartmentToDelete(dept)}
                                                                className="text-red-600 hover:text-red-900"
                                                                title="Delete"
                                                            >
                                                                🗑️
                                                            </button>
                                                        </td>
                                                    </tr>
                                                ))
                                            )}
                                            </tbody>
                                        </table>
                                    </div>

                                    <Pagination
                                        currentPage={departmentsPage}
                                        totalPages={departmentsTotalPages}
                                        onPageChange={setDepartmentsPage}
                                    />
                                </>
                            ) : (
                                <>
                                    <div className="flex justify-between items-center mb-4">
                                        <input
                                            type="text"
                                            placeholder="Search majors..."
                                            value={majorsSearch}
                                            onChange={(e) => {
                                                setMajorsSearch(e.target.value)
                                                setMajorsPage(0)
                                            }}
                                            className="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500 w-64"
                                        />
                                        <button
                                            onClick={() => setMajorModal({ open: true, mode: 'create', data: null })}
                                            className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
                                        >
                                            Create Major
                                        </button>
                                    </div>

                                    <div className="bg-white shadow overflow-hidden sm:rounded-md">
                                        <table className="min-w-full divide-y divide-gray-200">
                                            <thead className="bg-gray-50">
                                            <tr>
                                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Name</th>
                                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Department</th>
                                                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                                            </tr>
                                            </thead>
                                            <tbody className="bg-white divide-y divide-gray-200">
                                            {loading ? (
                                                <tr>
                                                    <td colSpan="4" className="px-6 py-4 text-center">Loading...</td>
                                                </tr>
                                            ) : majors.length === 0 ? (
                                                <tr>
                                                    <td colSpan="4" className="px-6 py-4 text-center text-gray-500">No majors found</td>
                                                </tr>
                                            ) : (
                                                majors.map((major) => (
                                                    <tr key={major.id}>
                                                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{major.id}</td>
                                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{major.name}</td>
                                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{major.departmentName}</td>
                                                        <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                                            <button
                                                                onClick={() => setMajorModal({ open: true, mode: 'update', data: major })}
                                                                className="text-green-600 hover:text-green-900 mr-3"
                                                                title="Edit"
                                                            >
                                                                ✏️
                                                            </button>
                                                            <button
                                                                onClick={() => setMajorToDelete(major)}
                                                                className="text-red-600 hover:text-red-900"
                                                                title="Delete"
                                                            >
                                                                🗑️
                                                            </button>
                                                        </td>
                                                    </tr>
                                                ))
                                            )}
                                            </tbody>
                                        </table>
                                    </div>

                                    <Pagination
                                        currentPage={majorsPage}
                                        totalPages={majorsTotalPages}
                                        onPageChange={setMajorsPage}
                                    />
                                </>
                            )}
                        </div>
                    </div>
                </main>
            </div>

            <DepartmentModalComponent />
            <MajorModalComponent />
            <ViewDepartmentModal />

            <ConfirmDialog
                isOpen={!!departmentToDelete}
                onClose={() => setDepartmentToDelete(null)}
                onConfirm={handleDeleteDepartment}
                title="Delete Department"
                message={`Are you sure you want to delete department "${departmentToDelete?.name}"? This action cannot be undone.`}
            />

            <ConfirmDialog
                isOpen={!!majorToDelete}
                onClose={() => setMajorToDelete(null)}
                onConfirm={handleDeleteMajor}
                title="Delete Major"
                message={`Are you sure you want to delete major "${majorToDelete?.name}"? This action cannot be undone.`}
            />
        </div>
    )
}