import { useState, useEffect } from 'react'
import Sidebar from '../../components/Layout/Sidebar'
import Navbar from '../../components/Layout/Navbar'
import Modal from '../../components/common/Modal'
import ConfirmDialog from '../../components/common/ConfirmDialog'
import Pagination from '../../components/common/Pagination'
import ErrorAlert from '../../components/common/ErrorAlert'
import { studentAPI } from '../../api/studentAPI'
import { lecturerAPI } from '../../api/lecturerAPI'
import { departmentAPI } from '../../api/departmentAPI'
import { majorAPI } from '../../api/majorAPI'

export default function AccountManagement() {
    const [activeTab, setActiveTab] = useState('students')
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')

    // Student state
    const [students, setStudents] = useState([])
    const [studentsTotalPages, setStudentsTotalPages] = useState(0)
    const [studentsPage, setStudentsPage] = useState(0)
    const [studentsSearch, setStudentsSearch] = useState('')
    const [studentModal, setStudentModal] = useState({ open: false, mode: 'create', data: null })
    const [studentToDelete, setStudentToDelete] = useState(null)
    const [majorsList, setMajorsList] = useState([])

    // Lecturer state
    const [lecturers, setLecturers] = useState([])
    const [lecturersTotalPages, setLecturersTotalPages] = useState(0)
    const [lecturersPage, setLecturersPage] = useState(0)
    const [lecturersSearch, setLecturersSearch] = useState('')
    const [lecturerModal, setLecturerModal] = useState({ open: false, mode: 'create', data: null })
    const [lecturerToDelete, setLecturerToDelete] = useState(null)
    const [departmentsList, setDepartmentsList] = useState([])

    useEffect(() => {
        if (activeTab === 'students') {
            fetchStudents()
            fetchMajorsList()
        } else {
            fetchLecturers()
            fetchDepartmentsList()
        }
    }, [activeTab, studentsPage, studentsSearch, lecturersPage, lecturersSearch])

    const fetchStudents = async () => {
        setLoading(true)
        try {
            const data = await studentAPI.getAllStudents(studentsSearch, studentsPage)
            setStudents(data.content)
            setStudentsTotalPages(data.totalPages)
        } catch (err) {
            setError('Failed to fetch students')
        } finally {
            setLoading(false)
        }
    }

    const fetchMajorsList = async () => {
        try {
            const data = await majorAPI.getAllMajorsList()
            setMajorsList(data)
        } catch (err) {
            console.error('Failed to fetch majors:', err)
        }
    }

    const fetchLecturers = async () => {
        setLoading(true)
        try {
            const data = await lecturerAPI.getAllLecturers(lecturersSearch, lecturersPage)
            setLecturers(data.content)
            setLecturersTotalPages(data.totalPages)
        } catch (err) {
            setError('Failed to fetch lecturers')
        } finally {
            setLoading(false)
        }
    }

    const fetchDepartmentsList = async () => {
        try {
            const data = await departmentAPI.getDepartmentsForDropdown()
            setDepartmentsList(data)
        } catch (err) {
            console.error('Failed to fetch departments:', err)
        }
    }

    const generateStudentCredentials = (id) => ({
        email: `${id}sv@dut.udn.vn`,
        password: `Dut@${id}`
    })

    const generateLecturerCredentials = (id) => ({
        email: `${id}gv@dut.udn.vn`,
        password: `Udn@${id}`
    })

    const handleCreateStudent = async (data) => {
        try {
            await studentAPI.createStudent(data)
            await fetchStudents()
            setStudentModal({ open: false, mode: 'create', data: null })
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to create student')
        }
    }

    const handleUpdateStudent = async (id, data) => {
        try {
            await studentAPI.updateStudent(id, data)
            await fetchStudents()
            setStudentModal({ open: false, mode: 'create', data: null })
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to update student')
        }
    }

    const handleResetStudentPassword = async (id) => {
        try {
            await studentAPI.resetStudentPassword(id)
            alert('Password reset successfully to default format: Dut@{studentId}')
        } catch (err) {
            setError('Failed to reset password')
        }
    }

    const handleDeleteStudent = async () => {
        try {
            await studentAPI.deleteStudent(studentToDelete.id)
            await fetchStudents()
            setStudentToDelete(null)
        } catch (err) {
            setError('Failed to delete student')
        }
    }

    const handleCreateLecturer = async (data) => {
        try {
            await lecturerAPI.createLecturer(data)
            await fetchLecturers()
            setLecturerModal({ open: false, mode: 'create', data: null })
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to create lecturer')
        }
    }

    const handleUpdateLecturer = async (id, data) => {
        try {
            await lecturerAPI.updateLecturer(id, data)
            await fetchLecturers()
            setLecturerModal({ open: false, mode: 'create', data: null })
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to update lecturer')
        }
    }

    const handleResetLecturerPassword = async (id) => {
        try {
            await lecturerAPI.resetLecturerPassword(id)
            alert('Password reset successfully to default format: Udn@{lecturerId}')
        } catch (err) {
            setError('Failed to reset password')
        }
    }

    const handleDeleteLecturer = async () => {
        try {
            await lecturerAPI.deleteLecturer(lecturerToDelete.id)
            await fetchLecturers()
            setLecturerToDelete(null)
        } catch (err) {
            setError('Failed to delete lecturer')
        }
    }

    const StudentModalComponent = () => {
        const [formData, setFormData] = useState({
            id: '', fullName: '', gender: '', dateOfBirth: '',
            phoneNumber: '', homeTown: '', homeClass: '', majorId: '',
            email: '', password: ''
        })

        useEffect(() => {
            const fetchStudentFullData = async () => {
                if (studentModal.mode === 'update' && studentModal.data) {
                    try {
                        // Fetch full student data by ID
                        const fullData = await studentAPI.getStudentById(studentModal.data.id)
                        setFormData({
                            id: fullData.id,
                            fullName: fullData.fullName || '',
                            gender: fullData.gender || '',
                            dateOfBirth: fullData.dateOfBirth || '',
                            phoneNumber: fullData.phoneNumber || '',
                            homeTown: fullData.homeTown || '',
                            homeClass: fullData.homeClass || '',
                            majorId: majorsList.find(m => m.name === fullData.majorName)?.id || '',
                            email: fullData.email || '',
                            password: ''
                        })
                    } catch (error) {
                        console.error('Failed to fetch student details:', error)
                        setError('Failed to load student details')
                    }
                } else if (studentModal.mode === 'create') {
                    setFormData({
                        id: '', fullName: '', gender: '', dateOfBirth: '',
                        phoneNumber: '', homeTown: '', homeClass: '', majorId: '',
                        email: '', password: ''
                    })
                }
            }

            if (studentModal.open) {
                fetchStudentFullData()
            }
        }, [studentModal.open, studentModal.mode, studentModal.data, majorsList])

        const handleIdChange = (id) => {
            const credentials = generateStudentCredentials(id)
            setFormData({ ...formData, id, email: credentials.email, password: credentials.password })
        }

        const handleSubmit = (e) => {
            e.preventDefault()
            const submitData = {
                id: formData.id,
                fullName: formData.fullName,
                gender: formData.gender,
                dateOfBirth: formData.dateOfBirth || null,
                phoneNumber: formData.phoneNumber || null,
                homeTown: formData.homeTown || null,
                homeClass: formData.homeClass || null,
                majorId: formData.majorId
            }
            if (studentModal.mode === 'create') {
                handleCreateStudent(submitData)
            } else {
                handleUpdateStudent(studentModal.data.id, submitData)
            }
        }

        const isUpdateMode = studentModal.mode === 'update'

        return (
            <Modal
                isOpen={studentModal.open}
                onClose={() => setStudentModal({ open: false, mode: 'create', data: null })}
                title={`${studentModal.mode === 'create' ? 'Create' : 'Update'} Student`}
                size="xl"
            >
                <form onSubmit={handleSubmit} className="space-y-4">
                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-700">Student ID *</label>
                            <input
                                type="text"
                                required
                                disabled={isUpdateMode}
                                value={formData.id}
                                onChange={(e) => handleIdChange(e.target.value)}
                                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 disabled:bg-gray-100"
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700">Full Name *</label>
                            <input
                                type="text"
                                required
                                value={formData.fullName}
                                onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
                                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700">Gender</label>
                            <select
                                value={formData.gender}
                                onChange={(e) => setFormData({ ...formData, gender: e.target.value })}
                                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                            >
                                <option value="">Select Gender</option>
                                <option value="MALE">Male</option>
                                <option value="FEMALE">Female</option>
                            </select>
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700">Date of Birth</label>
                            <input
                                type="date"
                                value={formData.dateOfBirth ? formData.dateOfBirth.split('T')[0] : ''}
                                onChange={(e) => setFormData({ ...formData, dateOfBirth: e.target.value })}
                                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700">Phone Number</label>
                            <input
                                type="text"
                                value={formData.phoneNumber}
                                onChange={(e) => setFormData({ ...formData, phoneNumber: e.target.value })}
                                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700">Home Town</label>
                            <input
                                type="text"
                                value={formData.homeTown}
                                onChange={(e) => setFormData({ ...formData, homeTown: e.target.value })}
                                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700">Home Class</label>
                            <input
                                type="text"
                                value={formData.homeClass}
                                onChange={(e) => setFormData({ ...formData, homeClass: e.target.value })}
                                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700">Major</label>
                            <select
                                required
                                value={formData.majorId}
                                onChange={(e) => setFormData({ ...formData, majorId: e.target.value })}
                                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                            >
                                <option value="">Select Major</option>
                                {majorsList.map(major => (
                                    <option key={major.id} value={major.id}>{major.name}</option>
                                ))}
                            </select>
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700">Email</label>
                            <input
                                type="email"
                                disabled
                                value={formData.email}
                                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm bg-gray-100"
                            />
                        </div>
                        {studentModal.mode === 'create' && (
                            <div>
                                <label className="block text-sm font-medium text-gray-700">Default Password</label>
                                <input
                                    type="text"
                                    disabled
                                    value={formData.password}
                                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm bg-gray-100"
                                />
                            </div>
                        )}
                    </div>

                    {isUpdateMode && (
                        <div className="flex justify-end">
                            <button
                                type="button"
                                onClick={() => handleResetStudentPassword(studentModal.data.id)}
                                className="px-4 py-2 text-sm font-medium text-white bg-yellow-600 rounded-md hover:bg-yellow-700"
                            >
                                Reset Password to Default
                            </button>
                        </div>
                    )}

                    <div className="flex justify-end space-x-3">
                        <button
                            type="button"
                            onClick={() => setStudentModal({ open: false, mode: 'create', data: null })}
                            className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700"
                        >
                            {studentModal.mode === 'create' ? 'Create' : 'Update'}
                        </button>
                    </div>
                </form>
            </Modal>
        )
    }

    const LecturerModalComponent = () => {
        const [formData, setFormData] = useState({
            id: '', fullName: '', gender: '', dateOfBirth: '',
            phoneNumber: '', homeTown: '', specialization: '',
            position: '', degree: '', departmentId: '',
            email: '', password: ''
        })

        useEffect(() => {
            const fetchLecturerFullData = async () => {
                if (lecturerModal.mode === 'update' && lecturerModal.data) {
                    try {
                        // Fetch full lecturer data by ID
                        const fullData = await lecturerAPI.getLecturerById(lecturerModal.data.id)
                        setFormData({
                            id: fullData.id,
                            fullName: fullData.fullName || '',
                            gender: fullData.gender || '',
                            dateOfBirth: fullData.dateOfBirth || '',
                            phoneNumber: fullData.phoneNumber || '',
                            homeTown: fullData.homeTown || '',
                            specialization: fullData.specialization || '',
                            position: fullData.position || '',
                            degree: fullData.degree || '',
                            departmentId: departmentsList.find(d => d.name === fullData.departmentName)?.id || '',
                            email: fullData.email || '',
                            password: ''
                        })
                    } catch (error) {
                        console.error('Failed to fetch lecturer details:', error)
                        setError('Failed to load lecturer details')
                    }
                } else if (lecturerModal.mode === 'create') {
                    setFormData({
                        id: '', fullName: '', gender: '', dateOfBirth: '',
                        phoneNumber: '', homeTown: '', specialization: '',
                        position: '', degree: '', departmentId: '',
                        email: '', password: ''
                    })
                }
            }

            if (lecturerModal.open) {
                fetchLecturerFullData()
            }
        }, [lecturerModal.open, lecturerModal.mode, lecturerModal.data, departmentsList])

        const handleIdChange = (id) => {
            const credentials = generateLecturerCredentials(id)
            setFormData({ ...formData, id, email: credentials.email, password: credentials.password })
        }

        const handleSubmit = (e) => {
            e.preventDefault()
            const submitData = {
                id: formData.id,
                fullName: formData.fullName,
                gender: formData.gender,
                dateOfBirth: formData.dateOfBirth || null,
                phoneNumber: formData.phoneNumber || null,
                homeTown: formData.homeTown || null,
                specialization: formData.specialization || null,
                position: formData.position || null,
                degree: formData.degree || null,
                departmentId: formData.departmentId
            }
            if (lecturerModal.mode === 'create') {
                handleCreateLecturer(submitData)
            } else {
                handleUpdateLecturer(lecturerModal.data.id, submitData)
            }
        }

        const isUpdateMode = lecturerModal.mode === 'update'

        return (
            <Modal
                isOpen={lecturerModal.open}
                onClose={() => setLecturerModal({ open: false, mode: 'create', data: null })}
                title={`${lecturerModal.mode === 'create' ? 'Create' : 'Update'} Lecturer`}
                size="xl"
            >
                <form onSubmit={handleSubmit} className="space-y-4">
                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-700">Lecturer ID *</label>
                            <input
                                type="text"
                                required
                                disabled={isUpdateMode}
                                value={formData.id}
                                onChange={(e) => handleIdChange(e.target.value)}
                                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 disabled:bg-gray-100"
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700">Full Name *</label>
                            <input
                                type="text"
                                required
                                value={formData.fullName}
                                onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
                                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700">Gender</label>
                            <select
                                value={formData.gender}
                                onChange={(e) => setFormData({ ...formData, gender: e.target.value })}
                                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                            >
                                <option value="">Select Gender</option>
                                <option value="MALE">Male</option>
                                <option value="FEMALE">Female</option>
                            </select>
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700">Date of Birth</label>
                            <input
                                type="date"
                                value={formData.dateOfBirth ? formData.dateOfBirth.split('T')[0] : ''}
                                onChange={(e) => setFormData({ ...formData, dateOfBirth: e.target.value })}
                                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700">Phone Number</label>
                            <input
                                type="text"
                                value={formData.phoneNumber}
                                onChange={(e) => setFormData({ ...formData, phoneNumber: e.target.value })}
                                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700">Home Town</label>
                            <input
                                type="text"
                                value={formData.homeTown}
                                onChange={(e) => setFormData({ ...formData, homeTown: e.target.value })}
                                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700">Specialization</label>
                            <input
                                type="text"
                                value={formData.specialization}
                                onChange={(e) => setFormData({ ...formData, specialization: e.target.value })}
                                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700">Position</label>
                            <input
                                type="text"
                                value={formData.position}
                                onChange={(e) => setFormData({ ...formData, position: e.target.value })}
                                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700">Degree</label>
                            <input
                                type="text"
                                value={formData.degree}
                                onChange={(e) => setFormData({ ...formData, degree: e.target.value })}
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
                                {departmentsList.map(dept => (
                                    <option key={dept.id} value={dept.id}>{dept.name}</option>
                                ))}
                            </select>
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700">Email</label>
                            <input
                                type="email"
                                disabled
                                value={formData.email}
                                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm bg-gray-100"
                            />
                        </div>
                        {lecturerModal.mode === 'create' && (
                            <div>
                                <label className="block text-sm font-medium text-gray-700">Default Password</label>
                                <input
                                    type="text"
                                    disabled
                                    value={formData.password}
                                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm bg-gray-100"
                                />
                            </div>
                        )}
                    </div>

                    {isUpdateMode && (
                        <div className="flex justify-end">
                            <button
                                type="button"
                                onClick={() => handleResetLecturerPassword(lecturerModal.data.id)}
                                className="px-4 py-2 text-sm font-medium text-white bg-yellow-600 rounded-md hover:bg-yellow-700"
                            >
                                Reset Password to Default
                            </button>
                        </div>
                    )}

                    <div className="flex justify-end space-x-3">
                        <button
                            type="button"
                            onClick={() => setLecturerModal({ open: false, mode: 'create', data: null })}
                            className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700"
                        >
                            {lecturerModal.mode === 'create' ? 'Create' : 'Update'}
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
                                    onClick={() => setActiveTab('students')}
                                    className={`py-2 px-1 border-b-2 font-medium text-sm ${
                                        activeTab === 'students'
                                            ? 'border-blue-500 text-blue-600'
                                            : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                                    }`}
                                >
                                    Manage Students
                                </button>
                                <button
                                    onClick={() => setActiveTab('lecturers')}
                                    className={`py-2 px-1 border-b-2 font-medium text-sm ${
                                        activeTab === 'lecturers'
                                            ? 'border-blue-500 text-blue-600'
                                            : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                                    }`}
                                >
                                    Manage Lecturers
                                </button>
                            </nav>
                        </div>

                        <div className="mt-6">
                            {activeTab === 'students' ? (
                                <>
                                    <div className="flex justify-between items-center mb-4">
                                        <input
                                            type="text"
                                            placeholder="Search students by ID or name..."
                                            value={studentsSearch}
                                            onChange={(e) => {
                                                setStudentsSearch(e.target.value)
                                                setStudentsPage(0)
                                            }}
                                            className="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500 w-96"
                                        />
                                        <button
                                            onClick={() => setStudentModal({ open: true, mode: 'create', data: null })}
                                            className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
                                        >
                                            Create Student
                                        </button>
                                    </div>

                                    <div className="bg-white shadow overflow-hidden sm:rounded-md">
                                        <table className="min-w-full divide-y divide-gray-200">
                                            <thead className="bg-gray-50">
                                            <tr>
                                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Full Name</th>
                                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Home Class</th>
                                                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                                            </tr>
                                            </thead>
                                            <tbody className="bg-white divide-y divide-gray-200">
                                            {loading ? (
                                                <tr>
                                                    <td colSpan="5" className="px-6 py-4 text-center">Loading...</td>
                                                </tr>
                                            ) : students.length === 0 ? (
                                                <tr>
                                                    <td colSpan="5" className="px-6 py-4 text-center text-gray-500">No students found</td>
                                                </tr>
                                            ) : (
                                                students.map((student) => (
                                                    <tr key={student.id}>
                                                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{student.id}</td>
                                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{student.fullName}</td>
                                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{student.homeClass || '-'}</td>
                                                        <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                                            <button
                                                                onClick={() => setStudentModal({ open: true, mode: 'update', data: student })}
                                                                className="text-green-600 hover:text-green-900 mr-3"
                                                                title="Edit"
                                                            >
                                                                ✏️
                                                            </button>
                                                            <button
                                                                onClick={() => setStudentToDelete(student)}
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
                                        currentPage={studentsPage}
                                        totalPages={studentsTotalPages}
                                        onPageChange={setStudentsPage}
                                    />
                                </>
                            ) : (
                                <>
                                    <div className="flex justify-between items-center mb-4">
                                        <input
                                            type="text"
                                            placeholder="Search lecturers by ID or name..."
                                            value={lecturersSearch}
                                            onChange={(e) => {
                                                setLecturersSearch(e.target.value)
                                                setLecturersPage(0)
                                            }}
                                            className="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500 w-96"
                                        />
                                        <button
                                            onClick={() => setLecturerModal({ open: true, mode: 'create', data: null })}
                                            className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
                                        >
                                            Create Lecturer
                                        </button>
                                    </div>

                                    <div className="bg-white shadow overflow-hidden sm:rounded-md">
                                        <table className="min-w-full divide-y divide-gray-200">
                                            <thead className="bg-gray-50">
                                            <tr>
                                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Full Name</th>
                                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Degree</th>
                                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Department</th>
                                                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                                            </tr>
                                            </thead>
                                            <tbody className="bg-white divide-y divide-gray-200">
                                            {loading ? (
                                                <tr>
                                                    <td colSpan="5" className="px-6 py-4 text-center">Loading...</td>
                                                </tr>
                                            ) : lecturers.length === 0 ? (
                                                <tr>
                                                    <td colSpan="5" className="px-6 py-4 text-center text-gray-500">No lecturers found</td>
                                                </tr>
                                            ) : (
                                                lecturers.map((lecturer) => (
                                                    <tr key={lecturer.id}>
                                                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{lecturer.id}</td>
                                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{lecturer.fullName}</td>
                                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{lecturer.degree || '-'}</td>
                                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{lecturer.departmentName}</td>
                                                        <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                                            <button
                                                                onClick={() => setLecturerModal({ open: true, mode: 'update', data: lecturer })}
                                                                className="text-green-600 hover:text-green-900 mr-3"
                                                                title="Edit"
                                                            >
                                                                ✏️
                                                            </button>
                                                            <button                                                                    onClick={() => setLecturerToDelete(lecturer)}
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
                                        currentPage={lecturersPage}
                                        totalPages={lecturersTotalPages}
                                        onPageChange={setLecturersPage}
                                    />
                                </>
                            )}
                        </div>
                    </div>
                </main>
            </div>

            <StudentModalComponent />
            <LecturerModalComponent />

            <ConfirmDialog
                isOpen={!!studentToDelete}
                onClose={() => setStudentToDelete(null)}
                onConfirm={handleDeleteStudent}
                title="Delete Student"
                message={`Are you sure you want to delete student "${studentToDelete?.fullName}"? This action cannot be undone.`}
            />

            <ConfirmDialog
                isOpen={!!lecturerToDelete}
                onClose={() => setLecturerToDelete(null)}
                onConfirm={handleDeleteLecturer}
                title="Delete Lecturer"
                message={`Are you sure you want to delete lecturer "${lecturerToDelete?.fullName}"? This action cannot be undone.`}
            />
        </div>
    )
}