import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'
import Sidebar from '../components/Layout/Sidebar'
import Navbar from '../components/Layout/Navbar'
import LoadingSpinner from '../components/common/LoadingSpinner'
import ErrorAlert from '../components/common/ErrorAlert'
import ConfirmDialog from '../components/common/ConfirmDialog'
import Modal from '../components/common/Modal'
import { pblClassAPI } from '../api/pblClassAPI'
import { pblGroupAPI } from '../api/pblGroupAPI'
import { projectAPI } from '../api/projectAPI'
import { majorAPI } from '../api/majorAPI'
import { progressTaskAPI } from '../api/progressTaskAPI'
import { finalReportAPI } from '../api/finalReportAPI'
import GroupCard from '../components/PblClass/GroupCard'
import ProjectCard from '../components/PblClass/ProjectCard'

export default function PblClassPage() {
    const { classId } = useParams()
    const { user } = useAuth()
    const navigate = useNavigate()
    const role = user?.role?.toUpperCase()
    const isLecturer = role === 'LECTURER'
    const isStudent = role === 'STUDENT'

    const [pblClass, setPblClass] = useState(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [activeTab, setActiveTab] = useState('groups')

    // Group state
    const [groups, setGroups] = useState([])
    const [groupsLoading, setGroupsLoading] = useState(false)

    // Project state
    const [projects, setProjects] = useState([])
    const [projectsLoading, setProjectsLoading] = useState(false)

    // Members state
    const [members, setMembers] = useState([])
    const [membersLoading, setMembersLoading] = useState(false)
    const [availableStudents, setAvailableStudents] = useState([])
    const [showAddStudentsModal, setShowAddStudentsModal] = useState(false)
    const [selectedStudents, setSelectedStudents] = useState([])

    // Task state
    const [tasks, setTasks] = useState([])
    const [tasksLoading, setTasksLoading] = useState(false)
    const [showTaskModal, setShowTaskModal] = useState(false)
    const [editingTask, setEditingTask] = useState(null)
    const [taskForm, setTaskForm] = useState({
        title: '',
        description: '',
        dueDate: ''
    })
    const [taskSubmitting, setTaskSubmitting] = useState(false)
    const [deleteTaskConfirm, setDeleteTaskConfirm] = useState(null)

    // Final Report state
    const [finalReport, setFinalReport] = useState(null)
    const [finalReportLoading, setFinalReportLoading] = useState(false)
    const [allReports, setAllReports] = useState([])
    const [allReportsLoading, setAllReportsLoading] = useState(false)
    const [showFinalReportModal, setShowFinalReportModal] = useState(false)
    const [finalReportForm, setFinalReportForm] = useState({
        title: '',
        description: '',
        file: null
    })
    const [finalReportSubmitting, setFinalReportSubmitting] = useState(false)
    const [selectedReportDetail, setSelectedReportDetail] = useState(null)
    const [showReportDetailModal, setShowReportDetailModal] = useState(false)

    // Student detail modal
    const [selectedStudent, setSelectedStudent] = useState(null)
    const [showStudentModal, setShowStudentModal] = useState(false)

    // Project detail modal
    const [selectedProject, setSelectedProject] = useState(null)
    const [showProjectModal, setShowProjectModal] = useState(false)

    // Project create/edit modal
    const [showProjectFormModal, setShowProjectFormModal] = useState(false)
    const [editingProject, setEditingProject] = useState(null)
    const [projectForm, setProjectForm] = useState({ title: '', description: '' })
    const [projectSubmitting, setProjectSubmitting] = useState(false)

    // Group project selection modal
    const [showGroupProjectModal, setShowGroupProjectModal] = useState(false)
    const [selectedGroupForProject, setSelectedGroupForProject] = useState(null)

    // Delete confirmations
    const [deleteClassConfirm, setDeleteClassConfirm] = useState(false)
    const [deleteGroupConfirm, setDeleteGroupConfirm] = useState(null)
    const [deleteProjectConfirm, setDeleteProjectConfirm] = useState(null)
    const [removeStudentConfirm, setRemoveStudentConfirm] = useState(null)
    const [removeGroupStudentConfirm, setRemoveGroupStudentConfirm] = useState(null)

    // Update class modal
    const [showUpdateClassModal, setShowUpdateClassModal] = useState(false)
    const [majorsList, setMajorsList] = useState([])
    const [updateClassForm, setUpdateClassForm] = useState({
        id: '',
        className: '',
        semester: '',
        maxStudentsPerGroup: 3,
        majorId: [],
        finalReportDeadline: ''
    })
    const [updatingClass, setUpdatingClass] = useState(false)

    useEffect(() => {
        fetchPblClass()
    }, [classId])

    useEffect(() => {
        if (activeTab === 'groups') {
            fetchGroups()
        } else if (activeTab === 'projects') {
            fetchProjects()
        } else if (activeTab === 'members') {
            fetchMembers()
        } else if (activeTab === 'progress') {
            fetchTasks()
        } else if (activeTab === 'final-report') {
            if (isStudent) {
                fetchMyFinalReport()
            } else if (isLecturer) {
                fetchAllReports()
            }
        }
    }, [activeTab])

    const fetchPblClass = async () => {
        setLoading(true)
        try {
            const data = await pblClassAPI.getPblClassById(classId)
            setPblClass(data)
            // Initialize update form
            setUpdateClassForm({
                id: data.id,
                className: data.className,
                semester: data.semester,
                maxStudentsPerGroup: data.maxStudentsPerGroup,
                majorId: [],
                finalReportDeadline: data.finalReportDeadline ? data.finalReportDeadline.slice(0, 16) : ''
            })
        } catch (err) {
            setError('Failed to load class details')
        } finally {
            setLoading(false)
        }
    }

    const fetchGroups = async () => {
        setGroupsLoading(true)
        try {
            const data = await pblGroupAPI.getGroupsByClass(classId)
            setGroups(data)
        } catch (err) {
            console.error('Failed to load groups:', err)
        } finally {
            setGroupsLoading(false)
        }
    }

    const fetchProjects = async () => {
        setProjectsLoading(true)
        try {
            const data = await projectAPI.getProjectsByPblClass(classId)
            setProjects(data)
        } catch (err) {
            console.error('Failed to load projects:', err)
        } finally {
            setProjectsLoading(false)
        }
    }

    const fetchMembers = async () => {
        setMembersLoading(true)
        try {
            const data = await pblClassAPI.getEnrolledStudents(classId)
            setMembers(data)
        } catch (err) {
            console.error('Failed to load members:', err)
        } finally {
            setMembersLoading(false)
        }
    }

    const fetchAvailableStudents = async () => {
        try {
            const data = await pblClassAPI.getAvailableStudents(classId)
            setAvailableStudents(data)
        } catch (err) {
            console.error('Failed to load available students:', err)
        }
    }

    const fetchMajorsForUpdate = async () => {
        if (!pblClass?.lecturerName) return
        try {
            // Fetch departments first, then majors by department
            // For now, we'll just show majors without department filter in update modal
            const response = await majorAPI.getAllMajorsList()
            setMajorsList(response || [])
        } catch (err) {
            console.error('Failed to load majors:', err)
        }
    }

    const fetchTasks = async () => {
        setTasksLoading(true)
        try {
            const data = await progressTaskAPI.getTasksByClass(classId)
            setTasks(data)
        } catch (err) {
            console.error('Failed to load tasks:', err)
        } finally {
            setTasksLoading(false)
        }
    }

    // Final Report functions
    const fetchMyFinalReport = async () => {
        setFinalReportLoading(true)
        try {
            const data = await finalReportAPI.getMyGroupReport(classId)
            setFinalReport(data)
        } catch (err) {
            if (err.response?.status !== 404) {
                console.error('Failed to load final report:', err)
            }
            setFinalReport(null)
        } finally {
            setFinalReportLoading(false)
        }
    }

    const fetchAllReports = async () => {
        if (!isLecturer) return
        setAllReportsLoading(true)
        try {
            const data = await finalReportAPI.getAllReportsForClass(classId)
            setAllReports(data)
        } catch (err) {
            console.error('Failed to load all reports:', err)
        } finally {
            setAllReportsLoading(false)
        }
    }

    const handleOpenFinalReportModal = () => {
        if (finalReport) {
            setFinalReportForm({
                title: finalReport.title,
                description: finalReport.description || '',
                file: null
            })
        } else {
            setFinalReportForm({
                title: '',
                description: '',
                file: null
            })
        }
        setShowFinalReportModal(true)
    }

    const handleFinalReportFileChange = (e) => {
        const file = e.target.files[0]
        if (file) {
            const fileName = file.name.toLowerCase()
            if (fileName.endsWith('.pdf') || fileName.endsWith('.docx') || fileName.endsWith('.ppt') || fileName.endsWith('.pptx')) {
                setFinalReportForm({ ...finalReportForm, file })
            } else {
                alert('Only PDF, DOCX, and PPT/PPTX files are allowed')
                e.target.value = ''
            }
        }
    }

    const handleSubmitFinalReport = async (e) => {
        e.preventDefault()
        if (!finalReportForm.title.trim()) {
            alert('Please enter a title')
            return
        }
        if (!finalReportForm.file && !finalReport) {
            alert('Please select a file to upload')
            return
        }

        setFinalReportSubmitting(true)
        try {
            await finalReportAPI.createOrUpdateReport(
                classId,
                finalReportForm.title,
                finalReportForm.description,
                finalReportForm.file
            )
            setShowFinalReportModal(false)
            await fetchMyFinalReport()
            if (isLecturer) {
                await fetchAllReports()
            }
            alert('Final report saved successfully!')
        } catch (err) {
            alert(err.response?.data?.message || 'Failed to save final report')
        } finally {
            setFinalReportSubmitting(false)
        }
    }

    const handleTogglePublicStatus = async () => {
        try {
            await finalReportAPI.togglePublicStatus(classId, finalReport.id)
            await fetchMyFinalReport()
            alert(finalReport.isPublic ? 'Report is now private' : 'Report is now public in the library')
        } catch (err) {
            alert(err.response?.data?.message || 'Failed to change public status')
        }
    }

    const handleDownloadReport = async (reportId, isMyReport = false) => {
        try {
            const response = await finalReportAPI.downloadReport(classId, reportId)
            const contentDisposition = response.headers['content-disposition']
            let filename = 'report'
            if (contentDisposition) {
                const match = contentDisposition.match(/filename="?(.+)"?/)
                if (match) filename = match[1]
            }

            const url = window.URL.createObjectURL(new Blob([response.data]))
            const link = document.createElement('a')
            link.href = url
            link.setAttribute('download', filename)
            document.body.appendChild(link)
            link.click()
            link.remove()
            window.URL.revokeObjectURL(url)
        } catch (err) {
            alert('Failed to download report')
        }
    }

    const handleViewReportDetail = async (reportId) => {
        try {
            const data = await finalReportAPI.getReportById(classId, reportId)
            setSelectedReportDetail(data)
            setShowReportDetailModal(true)
        } catch (err) {
            alert('Failed to load report details')
        }
    }

    const handleCreateTask = () => {
        setEditingTask(null)
        setTaskForm({ title: '', description: '', dueDate: '' })
        setShowTaskModal(true)
    }

    const handleEditTask = (task) => {
        setEditingTask(task)
        setTaskForm({
            title: task.title,
            description: task.description || '',
            dueDate: task.dueDate ? task.dueDate.slice(0, 16) : ''
        })
        setShowTaskModal(true)
    }

    const handleSubmitTask = async (e) => {
        e.preventDefault()
        if (!taskForm.title.trim()) {
            alert('Please enter a task title')
            return
        }
        if (!taskForm.dueDate) {
            alert('Please select a due date')
            return
        }

        setTaskSubmitting(true)
        try {
            if (editingTask) {
                await progressTaskAPI.updateTask(classId, editingTask.id, taskForm)
            } else {
                await progressTaskAPI.createTask(classId, taskForm)
            }
            setShowTaskModal(false)
            await fetchTasks()
        } catch (err) {
            alert(err.response?.data?.message || 'Failed to save task')
        } finally {
            setTaskSubmitting(false)
        }
    }

    const handleDeleteTask = async (taskId) => {
        try {
            await progressTaskAPI.deleteTask(classId, taskId)
            setDeleteTaskConfirm(null)
            await fetchTasks()
        } catch (err) {
            alert(err.response?.data?.message || 'Failed to delete task')
        }
    }

    const handleCreateGroup = async () => {
        try {
            const newGroup = await pblGroupAPI.createGroup(classId)
            await fetchGroups()
        } catch (err) {
            const message = err.response?.data?.message || 'Failed to create group'
            if (message.includes('already in a group')) {
                alert('You are already in a group!')
            } else {
                alert(message)
            }
        }
    }

    const handleJoinGroup = async (groupId) => {
        try {
            await pblGroupAPI.joinGroup(classId, groupId)
            await fetchGroups()
        } catch (err) {
            alert(err.response?.data?.message || 'Failed to join group')
        }
    }

    const handleDisbandGroup = async (groupId) => {
        try {
            await pblGroupAPI.disbandGroup(classId, groupId)
            await fetchGroups()
        } catch (err) {
            alert(err.response?.data?.message || 'Failed to disband group')
        }
    }

    const handleDeleteGroup = async (groupId) => {
        try {
            await pblGroupAPI.deleteGroup(classId, groupId)
            setDeleteGroupConfirm(null)
            await fetchGroups()
        } catch (err) {
            alert(err.response?.data?.message || 'Failed to delete group')
        }
    }

    const handleRemoveStudentFromGroup = async (groupId, studentId) => {
        try {
            await pblGroupAPI.removeStudentFromGroup(classId, groupId, studentId)
            setRemoveGroupStudentConfirm(null)
            await fetchGroups()
        } catch (err) {
            alert(err.response?.data?.message || 'Failed to remove student from group')
        }
    }

    const handleRemoveStudentFromClass = async (studentId) => {
        try {
            await pblClassAPI.removeStudentFromClass(classId, studentId)
            setRemoveStudentConfirm(null)
            await fetchMembers()
        } catch (err) {
            alert(err.response?.data?.message || 'Failed to remove student')
        }
    }

    const handleAddStudentsToClass = async () => {
        if (selectedStudents.length === 0) {
            alert('Please select at least one student')
            return
        }
        try {
            await pblClassAPI.addStudentsToClass(classId, selectedStudents)
            setShowAddStudentsModal(false)
            setSelectedStudents([])
            await fetchMembers()
        } catch (err) {
            alert(err.response?.data?.message || 'Failed to add students')
        }
    }

    const handleOpenAddStudentsModal = async () => {
        await fetchAvailableStudents()
        setSelectedStudents([])
        setShowAddStudentsModal(true)
    }

    const handleStudentCheckbox = (studentId) => {
        setSelectedStudents(prev =>
            prev.includes(studentId)
                ? prev.filter(id => id !== studentId)
                : [...prev, studentId]
        )
    }

    const handleViewStudentDetail = async (studentId) => {
        try {
            const data = await pblClassAPI.getStudentInClass(classId, studentId)
            setSelectedStudent(data)
            setShowStudentModal(true)
        } catch (err) {
            alert('Failed to load student details')
        }
    }

    const handleOpenGroupProjectModal = (group) => {
        setSelectedGroupForProject(group)
        setShowGroupProjectModal(true)
    }

    const handleSelectGroupProject = async (projectId) => {
        try {
            await pblGroupAPI.updateGroupProject(classId, selectedGroupForProject.id, projectId)
            setShowGroupProjectModal(false)
            setSelectedGroupForProject(null)
            await fetchGroups()
        } catch (err) {
            alert(err.response?.data?.message || 'Failed to update group project')
        }
    }

    const handleOpenProjectModal = async (project) => {
        try {
            const fullProject = await projectAPI.getProjectById(classId, project.id)
            setSelectedProject(fullProject)
            setShowProjectModal(true)
        } catch (err) {
            alert('Failed to load project details')
        }
    }

    const handleCreateProject = () => {
        setEditingProject(null)
        setProjectForm({ title: '', description: '' })
        setShowProjectFormModal(true)
    }

    const handleEditProject = async (project) => {
        try {
            const fullProject = await projectAPI.getProjectById(classId, project.id)
            setEditingProject(fullProject)
            setProjectForm({
                title: fullProject.title,
                description: fullProject.description || ''
            })
            setShowProjectFormModal(true)
        } catch (err) {
            alert('Failed to load project details')
        }
    }

    const handleSubmitProject = async (e) => {
        e.preventDefault()
        setProjectSubmitting(true)
        try {
            if (editingProject) {
                await projectAPI.updateProject(classId, editingProject.id, projectForm)
            } else {
                await projectAPI.createProject(classId, projectForm)
            }
            setShowProjectFormModal(false)
            await fetchProjects()
        } catch (err) {
            alert(err.response?.data?.message || 'Failed to save project')
        } finally {
            setProjectSubmitting(false)
        }
    }

    const handleDeleteProject = async (projectId) => {
        try {
            await projectAPI.deleteProject(classId, projectId)
            setDeleteProjectConfirm(null)
            await fetchProjects()
        } catch (err) {
            alert(err.response?.data?.message || 'Failed to delete project')
        }
    }

    const handleDeleteClass = async () => {
        try {
            await pblClassAPI.deletePblClass(classId)
            navigate('/dashboard')
        } catch (err) {
            alert(err.response?.data?.message || 'Failed to delete class')
            setDeleteClassConfirm(false)
        }
    }

    const handleUpdateClass = async (e) => {
        e.preventDefault()
        setUpdatingClass(true)
        try {
            await pblClassAPI.updatePblClass(classId, updateClassForm)
            setShowUpdateClassModal(false)
            await fetchPblClass()
        } catch (err) {
            alert(err.response?.data?.message || 'Failed to update class')
        } finally {
            setUpdatingClass(false)
        }
    }

    const openUpdateClassModal = async () => {
        await fetchMajorsForUpdate()
        setShowUpdateClassModal(true)
    }

    const getCurrentUserGroup = () => {
        return groups.find(group =>
            group.members?.some(member => member.id === user?.id)
        )
    }

    const tabs = [
        { id: 'groups', label: 'Group', icon: GroupsIcon },
        { id: 'projects', label: 'Project', icon: ProjectIcon },
        { id: 'progress', label: 'Progress Task', icon: ProgressIcon },
        { id: 'final-report', label: 'Final Report', icon: FinalReportIcon },
        { id: 'members', label: 'Members', icon: MembersIcon }
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

    if (!pblClass) {
        return (
            <div className="flex h-screen bg-gray-50">
                <Sidebar />
                <div className="flex-1 flex flex-col">
                    <Navbar />
                    <div className="flex-1 flex items-center justify-center">
                        <ErrorAlert message="Class not found" />
                    </div>
                </div>
            </div>
        )
    }

    const currentUserGroup = getCurrentUserGroup()
    const isUserInGroup = !!currentUserGroup

    return (
        <div className="flex h-screen bg-gray-50">
            <Sidebar />
            <div className="flex-1 flex flex-col overflow-hidden">
                <Navbar />
                <main className="flex-1 overflow-y-auto">
                    <div className="max-w-7xl mx-auto px-6 py-6">
                        {/* Back button */}
                        <button
                            onClick={() => navigate('/dashboard')}
                            className="mb-4 flex items-center text-blue-600 hover:text-blue-700"
                        >
                            <svg className="w-5 h-5 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
                            </svg>
                            Back to Dashboard
                        </button>

                        {/* Class Description */}
                        <div className="bg-white rounded-lg shadow p-6 mb-6">
                            <div className="flex justify-between items-start">
                                <div>
                                    <h1 className="text-2xl font-bold text-gray-900">{pblClass.className}</h1>
                                    <div className="mt-2 space-y-1">
                                        <p className="text-gray-600">
                                            <span className="font-medium">Semester:</span> {pblClass.semester}
                                        </p>
                                        <p className="text-gray-600">
                                            <span className="font-medium">Lecturer:</span> {pblClass.lecturerName}
                                        </p>
                                        <p className="text-gray-600">
                                            <span className="font-medium">Majors:</span> {pblClass.majorNames?.join(', ') || 'N/A'}
                                        </p>
                                        <p className="text-gray-600">
                                            <span className="font-medium">Max students per group:</span> {pblClass.maxStudentsPerGroup}
                                        </p>
                                        <p className="text-gray-600">
                                            <span className="font-medium">Final report deadline:</span>{' '}
                                            {pblClass.finalReportDeadline ? new Date(pblClass.finalReportDeadline).toLocaleString() : 'Not set'}
                                        </p>
                                    </div>
                                </div>
                                {isLecturer && (
                                    <div className="flex space-x-2">
                                        <button
                                            onClick={openUpdateClassModal}
                                            className="px-3 py-1 text-sm bg-blue-600 text-white rounded hover:bg-blue-700"
                                        >
                                            Update Class Info
                                        </button>
                                        <button
                                            onClick={() => setDeleteClassConfirm(true)}
                                            className="px-3 py-1 text-sm bg-red-600 text-white rounded hover:bg-red-700"
                                        >
                                            Delete Class
                                        </button>
                                    </div>
                                )}
                            </div>
                        </div>

                        {/* Tabs */}
                        <div className="border-b border-gray-200 mb-6">
                            <nav className="flex space-x-8">
                                {tabs.map(tab => (
                                    <button
                                        key={tab.id}
                                        onClick={() => setActiveTab(tab.id)}
                                        className={`flex items-center space-x-2 py-4 px-1 border-b-2 font-medium text-sm transition-colors ${
                                            activeTab === tab.id
                                                ? 'border-blue-500 text-blue-600'
                                                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                                        }`}
                                    >
                                        <tab.icon className={`w-5 h-5 ${activeTab === tab.id ? 'text-blue-600' : 'text-gray-500'}`} />
                                        <span>{tab.label}</span>
                                    </button>
                                ))}
                            </nav>
                        </div>

                        {/* Tab Content */}
                        <div>
                            {/* Groups Tab */}
                            {activeTab === 'groups' && (
                                <div>
                                    {isStudent && !isUserInGroup && (
                                        <div className="mb-6">
                                            <button
                                                onClick={handleCreateGroup}
                                                className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
                                            >
                                                + Create New Group
                                            </button>
                                        </div>
                                    )}

                                    {groupsLoading ? (
                                        <div className="flex justify-center py-12">
                                            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                                        </div>
                                    ) : groups.length === 0 ? (
                                        <div className="text-center py-12 bg-white rounded-lg shadow">
                                            <p className="text-gray-500">No groups created yet.</p>
                                            {isStudent && !isUserInGroup && (
                                                <p className="text-gray-400 text-sm mt-2">Click "Create New Group" to get started!</p>
                                            )}
                                        </div>
                                    ) : (
                                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                                            {groups.map(group => (
                                                <GroupCard
                                                    key={group.id}
                                                    group={group}
                                                    currentUser={user}
                                                    isLecturer={isLecturer}
                                                    isStudent={isStudent}
                                                    isUserInGroup={isUserInGroup}
                                                    currentUserGroupId={currentUserGroup?.id}
                                                    maxStudentsPerGroup={pblClass.maxStudentsPerGroup}
                                                    projects={projects}
                                                    onJoinGroup={handleJoinGroup}
                                                    onDisbandGroup={handleDisbandGroup}
                                                    onDeleteGroup={handleDeleteGroup}
                                                    onRemoveStudentFromGroup={handleRemoveStudentFromGroup}
                                                    onOpenGroupProjectModal={handleOpenGroupProjectModal}
                                                    setDeleteGroupConfirm={setDeleteGroupConfirm}
                                                    setRemoveGroupStudentConfirm={setRemoveGroupStudentConfirm}
                                                    deleteGroupConfirm={deleteGroupConfirm}
                                                    removeGroupStudentConfirm={removeGroupStudentConfirm}
                                                />
                                            ))}
                                        </div>
                                    )}
                                </div>
                            )}

                            {/* Projects Tab */}
                            {activeTab === 'projects' && (
                                <div>
                                    {isLecturer && (
                                        <div className="mb-6">
                                            <button
                                                onClick={handleCreateProject}
                                                className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
                                            >
                                                + Create New Project
                                            </button>
                                        </div>
                                    )}

                                    {projectsLoading ? (
                                        <div className="flex justify-center py-12">
                                            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                                        </div>
                                    ) : projects.length === 0 ? (
                                        <div className="text-center py-12 bg-white rounded-lg shadow">
                                            <p className="text-gray-500">No projects created yet.</p>
                                            {isLecturer && (
                                                <p className="text-gray-400 text-sm mt-2">Click "Create New Project" to get started!</p>
                                            )}
                                        </div>
                                    ) : (
                                        <div className="space-y-3">
                                            {projects.map(project => (
                                                <ProjectCard
                                                    key={project.id}
                                                    project={project}
                                                    isLecturer={isLecturer}
                                                    onViewDetails={handleOpenProjectModal}
                                                    onEdit={handleEditProject}
                                                    onDelete={handleDeleteProject}
                                                    setDeleteProjectConfirm={setDeleteProjectConfirm}
                                                    deleteProjectConfirm={deleteProjectConfirm}
                                                />
                                            ))}
                                        </div>
                                    )}
                                </div>
                            )}

                            {/* Progress Task Tab */}
                            {activeTab === 'progress' && (
                                <div>
                                    {isLecturer && (
                                        <div className="mb-6">
                                            <button
                                                onClick={() => {
                                                    setEditingTask(null)
                                                    setTaskForm({ title: '', description: '', dueDate: '' })
                                                    setShowTaskModal(true)
                                                }}
                                                className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
                                            >
                                                + Create New Task
                                            </button>
                                        </div>
                                    )}

                                    {tasksLoading ? (
                                        <div className="flex justify-center py-12">
                                            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                                        </div>
                                    ) : tasks.length === 0 ? (
                                        <div className="text-center py-12 bg-white rounded-lg shadow">
                                            <ProgressIcon className="w-16 h-16 text-gray-400 mx-auto mb-4" />
                                            <p className="text-gray-500">No progress tasks created yet.</p>
                                            {isLecturer && (
                                                <p className="text-gray-400 text-sm mt-2">Click "Create New Task" to get started!</p>
                                            )}
                                        </div>
                                    ) : (
                                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                                            {tasks.map(task => (
                                                <div
                                                    key={task.id}
                                                    onClick={() => navigate(`/pbl-class/${classId}/task/${task.id}`)}
                                                    className="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow cursor-pointer overflow-hidden group relative"
                                                >
                                                    {isLecturer && (
                                                        <div className="absolute top-2 right-2 flex space-x-1 z-10">
                                                            <button
                                                                onClick={(e) => {
                                                                    e.stopPropagation()
                                                                    handleEditTask(task)
                                                                }}
                                                                className="p-2 bg-green-500 rounded-full opacity-0 group-hover:opacity-100 transition-opacity hover:bg-green-600"
                                                                title="Edit task"
                                                            >
                                                                <svg className="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                                                                </svg>
                                                            </button>
                                                            <button
                                                                onClick={(e) => {
                                                                    e.stopPropagation()
                                                                    setDeleteTaskConfirm(task)
                                                                }}
                                                                className="p-2 bg-red-500 rounded-full opacity-0 group-hover:opacity-100 transition-opacity hover:bg-red-600"
                                                                title="Delete task"
                                                            >
                                                                <svg className="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                                                </svg>
                                                            </button>
                                                        </div>
                                                    )}
                                                    <div className="p-6">
                                                        <h3 className="text-lg font-semibold text-gray-900 mb-2">{task.title}</h3>
                                                        <p className="text-sm text-gray-500">
                                                            Due: {new Date(task.dueDate).toLocaleString()}
                                                        </p>
                                                    </div>
                                                </div>
                                            ))}
                                        </div>
                                    )}
                                </div>
                            )}

                            {/* Final Report Tab */}
                            {activeTab === 'final-report' && (
                                <div>
                                    {/* Student View */}
                                    {isStudent && (
                                        <div>
                                            <div className="flex justify-between items-center mb-6">
                                                <h2 className="text-xl font-semibold text-gray-900">Your Group's Final Report</h2>
                                                <button
                                                    onClick={handleOpenFinalReportModal}
                                                    className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
                                                >
                                                    {finalReport ? 'Update Report' : 'Submit Report'}
                                                </button>
                                            </div>

                                            {finalReportLoading ? (
                                                <div className="flex justify-center py-12">
                                                    <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                                                </div>
                                            ) : !finalReport ? (
                                                <div className="bg-white rounded-lg shadow p-12 text-center">
                                                    <svg className="w-16 h-16 text-gray-400 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                                                    </svg>
                                                    <p className="text-gray-500">Your group hasn't submitted a final report yet.</p>
                                                    <button
                                                        onClick={handleOpenFinalReportModal}
                                                        className="mt-4 px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
                                                    >
                                                        Submit Your First Report
                                                    </button>
                                                </div>
                                            ) : (
                                                <div className="bg-white rounded-lg shadow p-6">
                                                    <div className="flex justify-between items-start">
                                                        <div className="flex-1">
                                                            <h3 className="text-xl font-bold text-gray-900 mb-2">{finalReport.title}</h3>
                                                            <p className="text-gray-700 whitespace-pre-wrap mb-4">{finalReport.description || 'No description'}</p>
                                                            <div className="space-y-2 text-sm">
                                                                <p><span className="font-medium">File Type:</span> {finalReport.fileType}</p>
                                                                <p><span className="font-medium">Submitted:</span> {new Date(finalReport.submittedAt).toLocaleString()}</p>
                                                                {finalReport.lastModifiedAt && (
                                                                    <p><span className="font-medium">Last Modified:</span> {new Date(finalReport.lastModifiedAt).toLocaleString()}</p>
                                                                )}
                                                            </div>
                                                        </div>
                                                        <div className="flex flex-col space-y-2 ml-4">
                                                            <button
                                                                onClick={() => handleDownloadReport(finalReport.id, true)}
                                                                className="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700"
                                                            >
                                                                Download
                                                            </button>
                                                            <button
                                                                onClick={handleTogglePublicStatus}
                                                                className={`px-4 py-2 rounded-md ${
                                                                    finalReport.isPublic
                                                                        ? 'bg-yellow-600 hover:bg-yellow-700'
                                                                        : 'bg-purple-600 hover:bg-purple-700'
                                                                } text-white`}
                                                            >
                                                                {finalReport.isPublic ? 'Make Private' : 'Publicize to Library'}
                                                            </button>
                                                        </div>
                                                    </div>
                                                    {finalReport.isPublic && (
                                                        <div className="mt-4 p-3 bg-green-50 rounded-md">
                                                            <p className="text-sm text-green-700">
                                                                ✓ This report is public and can be viewed in the library
                                                            </p>
                                                        </div>
                                                    )}
                                                </div>
                                            )}
                                        </div>
                                    )}

                                    {/* Lecturer View */}
                                    {isLecturer && (
                                        <div>
                                            <h2 className="text-xl font-semibold text-gray-900 mb-4">All Final Reports</h2>
                                            {allReportsLoading ? (
                                                <div className="flex justify-center py-12">
                                                    <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                                                </div>
                                            ) : allReports.length === 0 ? (
                                                <div className="bg-white rounded-lg shadow p-12 text-center">
                                                    <p className="text-gray-500">No final reports submitted yet.</p>
                                                </div>
                                            ) : (
                                                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                                    {allReports.map(report => (
                                                        <div
                                                            key={report.id}
                                                            className="bg-white rounded-lg shadow p-4 hover:shadow-md transition-shadow"
                                                        >
                                                            <div className="flex justify-between items-start">
                                                                <div className="flex-1">
                                                                    <h3 className="font-semibold text-gray-900">{report.title}</h3>
                                                                    <p className="text-sm text-gray-500">Group: {report.groupName}</p>
                                                                    <p className="text-sm text-gray-500">Submitted by: {report.submittedByStudentName}</p>
                                                                    <p className="text-xs text-gray-400 mt-1">
                                                                        {new Date(report.submittedAt).toLocaleString()}
                                                                    </p>
                                                                </div>
                                                                <div className="flex space-x-2 ml-4">
                                                                    <button
                                                                        onClick={() => handleDownloadReport(report.id)}
                                                                        className="p-2 text-green-600 hover:text-green-800"
                                                                        title="Download"
                                                                    >
                                                                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                                                                        </svg>
                                                                    </button>
                                                                    <button
                                                                        onClick={() => handleViewReportDetail(report.id)}
                                                                        className="p-2 text-blue-600 hover:text-blue-800"
                                                                        title="View Details"
                                                                    >
                                                                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                                                                        </svg>
                                                                    </button>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    ))}
                                                </div>
                                            )}
                                        </div>
                                    )}
                                </div>
                            )}

                            {/* Members Tab */}
                            {activeTab === 'members' && (
                                <div>
                                    {isLecturer && (
                                        <div className="mb-6">
                                            <button
                                                onClick={handleOpenAddStudentsModal}
                                                className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
                                            >
                                                + Add Students to Class
                                            </button>
                                        </div>
                                    )}

                                    {membersLoading ? (
                                        <div className="flex justify-center py-12">
                                            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                                        </div>
                                    ) : members.length === 0 ? (
                                        <div className="text-center py-12 bg-white rounded-lg shadow">
                                            <p className="text-gray-500">No students enrolled yet.</p>
                                            {isLecturer && (
                                                <p className="text-gray-400 text-sm mt-2">Click "Add Students to Class" to enroll students!</p>
                                            )}
                                        </div>
                                    ) : (
                                        <div className="bg-white rounded-lg shadow overflow-hidden">
                                            <table className="min-w-full divide-y divide-gray-200">
                                                <thead className="bg-gray-50">
                                                <tr>
                                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Student ID</th>
                                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Full Name</th>
                                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Home Class</th>
                                                    <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                                                </tr>
                                                </thead>
                                                <tbody className="bg-white divide-y divide-gray-200">
                                                {members.map((student) => (
                                                    <tr key={student.id} className="hover:bg-gray-50">
                                                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{student.id}</td>
                                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{student.fullName}</td>
                                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{student.homeClass || '-'}</td>
                                                        <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                                            <div className="flex justify-end space-x-2">
                                                                <button
                                                                    onClick={() => handleViewStudentDetail(student.id)}
                                                                    className="text-blue-600 hover:text-blue-900"
                                                                    title="View details"
                                                                >
                                                                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                                                                    </svg>
                                                                </button>
                                                                {isLecturer && (
                                                                    <button
                                                                        onClick={() => setRemoveStudentConfirm(student.id)}
                                                                        className="text-red-600 hover:text-red-900"
                                                                        title="Remove student"
                                                                    >
                                                                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                                                        </svg>
                                                                    </button>
                                                                )}
                                                            </div>
                                                        </td>
                                                    </tr>
                                                ))}
                                                </tbody>
                                            </table>
                                        </div>
                                    )}
                                </div>
                            )}
                        </div>
                    </div>
                </main>
            </div>

            {/* Add Students Modal */}
            <Modal
                isOpen={showAddStudentsModal}
                onClose={() => setShowAddStudentsModal(false)}
                title="Add Students to Class"
                size="lg"
            >
                <div className="max-h-96 overflow-y-auto">
                    {availableStudents.length === 0 ? (
                        <p className="text-gray-500 text-center py-4">No available students to add.</p>
                    ) : (
                        <div className="space-y-2">
                            {availableStudents.map(student => (
                                <label key={student.id} className="flex items-center p-3 hover:bg-gray-50 rounded-lg cursor-pointer">
                                    <input
                                        type="checkbox"
                                        checked={selectedStudents.includes(student.id)}
                                        onChange={() => handleStudentCheckbox(student.id)}
                                        className="h-4 w-4 text-blue-600 rounded border-gray-300"
                                    />
                                    <div className="ml-3">
                                        <p className="text-sm font-medium text-gray-900">{student.fullName}</p>
                                        <p className="text-xs text-gray-500">{student.id} - {student.homeClass || 'No class'}</p>
                                    </div>
                                </label>
                            ))}
                        </div>
                    )}
                </div>
                <div className="mt-6 flex justify-end space-x-3">
                    <button
                        onClick={() => setShowAddStudentsModal(false)}
                        className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
                    >
                        Cancel
                    </button>
                    <button
                        onClick={handleAddStudentsToClass}
                        disabled={selectedStudents.length === 0}
                        className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 disabled:opacity-50"
                    >
                        Add Selected ({selectedStudents.length})
                    </button>
                </div>
            </Modal>

            {/* Student Detail Modal */}
            <Modal
                isOpen={showStudentModal}
                onClose={() => setShowStudentModal(false)}
                title="Student Details"
            >
                {selectedStudent && (
                    <div className="space-y-3">
                        <div className="grid grid-cols-2 gap-2">
                            <p className="text-sm text-gray-500">ID:</p>
                            <p className="text-sm text-gray-900">{selectedStudent.id}</p>
                            <p className="text-sm text-gray-500">Full Name:</p>
                            <p className="text-sm text-gray-900">{selectedStudent.fullName}</p>
                            <p className="text-sm text-gray-500">Email:</p>
                            <p className="text-sm text-gray-900">{selectedStudent.email}</p>
                            <p className="text-sm text-gray-500">Gender:</p>
                            <p className="text-sm text-gray-900">{selectedStudent.gender || '-'}</p>
                            <p className="text-sm text-gray-500">Date of Birth:</p>
                            <p className="text-sm text-gray-900">{selectedStudent.dateOfBirth ? new Date(selectedStudent.dateOfBirth).toLocaleDateString() : '-'}</p>
                            <p className="text-sm text-gray-500">Phone Number:</p>
                            <p className="text-sm text-gray-900">{selectedStudent.phoneNumber || '-'}</p>
                            <p className="text-sm text-gray-500">Home Town:</p>
                            <p className="text-sm text-gray-900">{selectedStudent.homeTown || '-'}</p>
                            <p className="text-sm text-gray-500">Home Class:</p>
                            <p className="text-sm text-gray-900">{selectedStudent.homeClass || '-'}</p>
                            <p className="text-sm text-gray-500">Major:</p>
                            <p className="text-sm text-gray-900">{selectedStudent.majorName || '-'}</p>
                        </div>
                    </div>
                )}
            </Modal>

            {/* Project Detail Modal */}
            <Modal
                isOpen={showProjectModal}
                onClose={() => setShowProjectModal(false)}
                title="Project Details"
            >
                {selectedProject && (
                    <div className="space-y-4">
                        <div>
                            <h3 className="text-lg font-medium text-gray-900">{selectedProject.title}</h3>
                            <p className="mt-2 text-sm text-gray-600 whitespace-pre-wrap">{selectedProject.description || 'No description'}</p>
                        </div>
                        <div>
                            <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${
                                selectedProject.status === 'AVAILABLE'
                                    ? 'bg-green-100 text-green-800'
                                    : 'bg-yellow-100 text-yellow-800'
                            }`}>
                                {selectedProject.status || 'AVAILABLE'}
                            </span>
                        </div>
                    </div>
                )}
            </Modal>

            {/* Project Form Modal (Create/Edit) */}
            <Modal
                isOpen={showProjectFormModal}
                onClose={() => setShowProjectFormModal(false)}
                title={editingProject ? 'Edit Project' : 'Create New Project'}
            >
                <form onSubmit={handleSubmitProject} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Title *</label>
                        <input
                            type="text"
                            required
                            value={projectForm.title}
                            onChange={(e) => setProjectForm({ ...projectForm, title: e.target.value })}
                            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Description</label>
                        <textarea
                            rows={5}
                            value={projectForm.description}
                            onChange={(e) => setProjectForm({ ...projectForm, description: e.target.value })}
                            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                        />
                    </div>
                    <div className="flex justify-end space-x-3">
                        <button
                            type="button"
                            onClick={() => setShowProjectFormModal(false)}
                            className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            disabled={projectSubmitting}
                            className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 disabled:opacity-50"
                        >
                            {projectSubmitting ? 'Saving...' : 'Save'}
                        </button>
                    </div>
                </form>
            </Modal>

            {/* Group Project Selection Modal */}
            <Modal
                isOpen={showGroupProjectModal}
                onClose={() => setShowGroupProjectModal(false)}
                title="Select Project for Group"
                size="lg"
            >
                <div className="space-y-2 max-h-96 overflow-y-auto">
                    {projects.length === 0 ? (
                        <p className="text-gray-500 text-center py-4">No projects available.</p>
                    ) : (
                        projects.map(project => {
                            const isTaken = project.projectStatus === 'TAKEN'
                            return (
                                <button
                                    key={project.id}
                                    onClick={() => !isTaken && handleSelectGroupProject(project.id)}
                                    disabled={isTaken}
                                    className={`w-full text-left p-4 rounded-lg border transition-colors ${
                                        isTaken
                                            ? 'bg-gray-100 border-gray-200 cursor-not-allowed opacity-60'
                                            : 'hover:bg-blue-50 border-gray-200 hover:border-blue-300'
                                    }`}
                                >
                                    <div className="flex justify-between items-center">
                                        <div>
                                            <p className="font-medium text-gray-900">{project.title}</p>
                                            <span className={`text-xs ${isTaken ? 'text-gray-500' : 'text-green-600'}`}>
                                                {isTaken ? 'Taken' : 'Available'}
                                            </span>
                                        </div>
                                        {!isTaken && (
                                            <svg className="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                                            </svg>
                                        )}
                                    </div>
                                </button>
                            )
                        })
                    )}
                </div>
            </Modal>

            {/* Update Class Modal */}
            <Modal
                isOpen={showUpdateClassModal}
                onClose={() => setShowUpdateClassModal(false)}
                title="Update Class Information"
                size="lg"
            >
                <form onSubmit={handleUpdateClass} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Class ID</label>
                        <input
                            type="text"
                            value={updateClassForm.id}
                            disabled
                            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md bg-gray-100"
                        />
                        <p className="text-xs text-gray-500 mt-1">Class ID cannot be changed</p>
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Class Name *</label>
                        <input
                            type="text"
                            required
                            value={updateClassForm.className}
                            onChange={(e) => setUpdateClassForm({ ...updateClassForm, className: e.target.value })}
                            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Semester *</label>
                        <input
                            type="text"
                            required
                            placeholder="e.g., Spring 2024 or 2024-2025"
                            value={updateClassForm.semester}
                            onChange={(e) => setUpdateClassForm({ ...updateClassForm, semester: e.target.value })}
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
                            value={updateClassForm.maxStudentsPerGroup}
                            onChange={(e) => setUpdateClassForm({ ...updateClassForm, maxStudentsPerGroup: parseInt(e.target.value) })}
                            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Final Report Deadline *</label>
                        <input
                            type="datetime-local"
                            required
                            value={updateClassForm.finalReportDeadline}
                            onChange={(e) => setUpdateClassForm({ ...updateClassForm, finalReportDeadline: e.target.value })}
                            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                        />
                    </div>
                    <div className="text-sm text-gray-500">
                        <p>Note: Majors cannot be changed after class creation.</p>
                    </div>
                    <div className="flex justify-end space-x-3 pt-4">
                        <button
                            type="button"
                            onClick={() => setShowUpdateClassModal(false)}
                            className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            disabled={updatingClass}
                            className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 disabled:opacity-50"
                        >
                            {updatingClass ? 'Updating...' : 'Update Class'}
                        </button>
                    </div>
                </form>
            </Modal>

            {/* Task Form Modal (Create/Edit) */}
            <Modal
                isOpen={showTaskModal}
                onClose={() => setShowTaskModal(false)}
                title={editingTask ? 'Edit Task' : 'Create New Task'}
                size="lg"
            >
                <form onSubmit={handleSubmitTask} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Title *</label>
                        <input
                            type="text"
                            required
                            value={taskForm.title}
                            onChange={(e) => setTaskForm({ ...taskForm, title: e.target.value })}
                            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                            placeholder="e.g., Research Proposal"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700">Description</label>
                        <textarea
                            rows={5}
                            value={taskForm.description}
                            onChange={(e) => setTaskForm({ ...taskForm, description: e.target.value })}
                            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                            placeholder="Describe the task requirements..."
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700">Due Date *</label>
                        <input
                            type="datetime-local"
                            required
                            value={taskForm.dueDate}
                            onChange={(e) => setTaskForm({ ...taskForm, dueDate: e.target.value })}
                            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                        />
                    </div>

                    <div className="flex justify-end space-x-3 pt-4">
                        <button
                            type="button"
                            onClick={() => setShowTaskModal(false)}
                            className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            disabled={taskSubmitting}
                            className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 disabled:opacity-50"
                        >
                            {taskSubmitting ? 'Saving...' : 'Save Task'}
                        </button>
                    </div>
                </form>
            </Modal>

            {/* Final Report Form Modal */}
            <Modal
                isOpen={showFinalReportModal}
                onClose={() => setShowFinalReportModal(false)}
                title={finalReport ? 'Update Final Report' : 'Submit Final Report'}
                size="lg"
            >
                <form onSubmit={handleSubmitFinalReport} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Title *</label>
                        <input
                            type="text"
                            required
                            value={finalReportForm.title}
                            onChange={(e) => setFinalReportForm({ ...finalReportForm, title: e.target.value })}
                            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                            placeholder="Enter report title"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700">Description</label>
                        <textarea
                            rows={4}
                            value={finalReportForm.description}
                            onChange={(e) => setFinalReportForm({ ...finalReportForm, description: e.target.value })}
                            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                            placeholder="Describe your report..."
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700">File * {finalReport && '(upload new file to replace)'}</label>
                        <input
                            type="file"
                            accept=".pdf,.docx,.ppt,.pptx"
                            onChange={handleFinalReportFileChange}
                            className="mt-1 block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-md file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100"
                        />
                        <p className="mt-1 text-xs text-gray-500">Accepted formats: PDF, DOCX, PPT/PPTX</p>
                    </div>

                    <div className="flex justify-end space-x-3 pt-4">
                        <button
                            type="button"
                            onClick={() => setShowFinalReportModal(false)}
                            className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            disabled={finalReportSubmitting}
                            className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 disabled:opacity-50"
                        >
                            {finalReportSubmitting ? 'Saving...' : 'Submit'}
                        </button>
                    </div>
                </form>
            </Modal>

            {/* Report Detail Modal (Lecturer) */}
            <Modal
                isOpen={showReportDetailModal}
                onClose={() => setShowReportDetailModal(false)}
                title="Final Report Details"
                size="lg"
            >
                {selectedReportDetail && (
                    <div className="space-y-4">
                        <div>
                            <h3 className="font-medium text-gray-900">Title</h3>
                            <p className="text-gray-700">{selectedReportDetail.title}</p>
                        </div>
                        <div>
                            <h3 className="font-medium text-gray-900">Description</h3>
                            <p className="text-gray-700 whitespace-pre-wrap">{selectedReportDetail.description || 'No description'}</p>
                        </div>
                        <div className="grid grid-cols-2 gap-2 text-sm">
                            <p className="text-gray-500">Group:</p>
                            <p className="text-gray-900">{selectedReportDetail.groupName}</p>
                            <p className="text-gray-500">Submitted by:</p>
                            <p className="text-gray-900">{selectedReportDetail.submittedBy?.fullName}</p>
                            <p className="text-gray-500">File Type:</p>
                            <p className="text-gray-900">{selectedReportDetail.fileType}</p>
                            <p className="text-gray-500">Submitted at:</p>
                            <p className="text-gray-900">{new Date(selectedReportDetail.submittedAt).toLocaleString()}</p>
                            {selectedReportDetail.lastModifiedBy && (
                                <>
                                    <p className="text-gray-500">Last modified by:</p>
                                    <p className="text-gray-900">{selectedReportDetail.lastModifiedBy?.fullName}</p>
                                </>
                            )}
                        </div>
                        <div className="pt-4">
                            <button
                                onClick={() => handleDownloadReport(selectedReportDetail.id)}
                                className="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700"
                            >
                                Download File
                            </button>
                        </div>
                    </div>
                )}
            </Modal>

            {/* Delete Task Confirm Dialog */}
            <ConfirmDialog
                isOpen={!!deleteTaskConfirm}
                onClose={() => setDeleteTaskConfirm(null)}
                onConfirm={() => handleDeleteTask(deleteTaskConfirm.id)}
                title="Delete Task"
                message={`Are you sure you want to delete task "${deleteTaskConfirm?.title}"? This will also delete all submissions for this task. This action cannot be undone.`}
            />

            {/* Delete Class Confirm Dialog */}
            <ConfirmDialog
                isOpen={deleteClassConfirm}
                onClose={() => setDeleteClassConfirm(false)}
                onConfirm={handleDeleteClass}
                title="Delete Class"
                message={`Are you sure you want to delete "${pblClass?.className}"? This action cannot be undone.`}
            />

            {/* Delete Group Confirm Dialog */}
            {deleteGroupConfirm && (
                <ConfirmDialog
                    isOpen={!!deleteGroupConfirm}
                    onClose={() => setDeleteGroupConfirm(null)}
                    onConfirm={() => handleDeleteGroup(deleteGroupConfirm.id)}
                    title="Delete Group"
                    message={`Are you sure you want to delete group "${deleteGroupConfirm.name}"? This action cannot be undone.`}
                />
            )}

            {/* Delete Project Confirm Dialog */}
            {deleteProjectConfirm && (
                <ConfirmDialog
                    isOpen={!!deleteProjectConfirm}
                    onClose={() => setDeleteProjectConfirm(null)}
                    onConfirm={() => handleDeleteProject(deleteProjectConfirm.id)}
                    title="Delete Project"
                    message={`Are you sure you want to delete project "${deleteProjectConfirm.title}"? This action cannot be undone.`}
                />
            )}

            {/* Remove Student from Class Confirm Dialog */}
            <ConfirmDialog
                isOpen={!!removeStudentConfirm}
                onClose={() => setRemoveStudentConfirm(null)}
                onConfirm={() => handleRemoveStudentFromClass(removeStudentConfirm)}
                title="Remove Student"
                message="Are you sure you want to remove this student from the class?"
            />

            {/* Remove Student from Group Confirm Dialog */}
            {removeGroupStudentConfirm && (
                <ConfirmDialog
                    isOpen={!!removeGroupStudentConfirm}
                    onClose={() => setRemoveGroupStudentConfirm(null)}
                    onConfirm={() => handleRemoveStudentFromGroup(removeGroupStudentConfirm.groupId, removeGroupStudentConfirm.studentId)}
                    title="Remove Student from Group"
                    message="Are you sure you want to remove this student from the group?"
                />
            )}
        </div>
    )
}

// Icon Components
function GroupsIcon({ className = "w-5 h-5" }) {
    return (
        <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
        </svg>
    )
}

function ProjectIcon({ className = "w-5 h-5" }) {
    return (
        <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" />
        </svg>
    )
}

function ProgressIcon({ className = "w-5 h-5" }) {
    return (
        <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01" />
        </svg>
    )
}

function FinalReportIcon({ className = "w-5 h-5" }) {
    return (
        <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
        </svg>
    )
}

function MembersIcon({ className = "w-5 h-5" }) {
    return (
        <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
        </svg>
    )
}