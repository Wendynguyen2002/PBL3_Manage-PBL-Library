import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'
import Sidebar from '../components/Layout/Sidebar'
import Navbar from '../components/Layout/Navbar'
import LoadingSpinner from '../components/common/LoadingSpinner'
import Modal from '../components/common/Modal'
import ConfirmDialog from '../components/common/ConfirmDialog'
import { progressTaskAPI } from '../api/progressTaskAPI'
import { taskSubmissionAPI } from '../api/taskSubmissionAPI'

export default function TaskPage() {
    const { classId, taskId } = useParams()
    const { user } = useAuth()
    const navigate = useNavigate()
    const role = user?.role?.toUpperCase()
    const isLecturer = role === 'LECTURER'
    const isStudent = role === 'STUDENT'

    const [task, setTask] = useState(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    // Student submission
    const [mySubmission, setMySubmission] = useState(null)
    const [showSubmissionModal, setShowSubmissionModal] = useState(false)
    const [submissionForm, setSubmissionForm] = useState({
        briefDescription: '',
        links: [{ url: '' }]
    })
    const [submitting, setSubmitting] = useState(false)

    // Lecturer submissions list
    const [allSubmissions, setAllSubmissions] = useState([])
    const [selectedSubmission, setSelectedSubmission] = useState(null)
    const [showSubmissionDetailModal, setShowSubmissionDetailModal] = useState(false)

    useEffect(() => {
        fetchTaskDetails()
        if (isStudent) {
            fetchMySubmission()
        } else if (isLecturer) {
            fetchAllSubmissions()
        }
    }, [taskId])

    const fetchTaskDetails = async () => {
        setLoading(true)
        try {
            const data = await progressTaskAPI.getTaskById(classId, taskId)
            setTask(data)
        } catch (err) {
            setError('Failed to load task details')
        } finally {
            setLoading(false)
        }
    }

    const fetchMySubmission = async () => {
        try {
            const data = await taskSubmissionAPI.getMyGroupSubmission(classId, taskId)
            setMySubmission(data)
        } catch (err) {
            if (err.response?.status !== 404) {
                console.error('Failed to load submission:', err)
            }
        }
    }

    const fetchAllSubmissions = async () => {
        try {
            const data = await taskSubmissionAPI.getAllSubmissionsForTask(classId, taskId)
            setAllSubmissions(data)
        } catch (err) {
            console.error('Failed to load submissions:', err)
        }
    }

    const handleSubmitSubmission = async (e) => {
        e.preventDefault()
        if (!submissionForm.briefDescription.trim()) {
            alert('Please enter a brief description')
            return
        }

        // Filter out empty links
        const validLinks = submissionForm.links.filter(link => link.url.trim())
        const data = {
            briefDescription: submissionForm.briefDescription,
            links: validLinks
        }

        setSubmitting(true)
        try {
            await taskSubmissionAPI.submitOrUpdateSubmission(classId, taskId, data)
            setShowSubmissionModal(false)
            await fetchMySubmission()
            alert('Submission saved successfully!')
        } catch (err) {
            alert(err.response?.data?.message || 'Failed to submit')
        } finally {
            setSubmitting(false)
        }
    }

    const handleAddLink = () => {
        setSubmissionForm({
            ...submissionForm,
            links: [...submissionForm.links, { url: '' }]
        })
    }

    const handleRemoveLink = (index) => {
        const newLinks = submissionForm.links.filter((_, i) => i !== index)
        setSubmissionForm({ ...submissionForm, links: newLinks })
    }

    const handleLinkChange = (index, value) => {
        const newLinks = [...submissionForm.links]
        newLinks[index].url = value
        setSubmissionForm({ ...submissionForm, links: newLinks })
    }

    const openSubmissionModal = () => {
        if (mySubmission) {
            setSubmissionForm({
                briefDescription: mySubmission.briefDescription || '',
                links: mySubmission.links?.length ? mySubmission.links.map(l => ({ url: l.url })) : [{ url: '' }]
            })
        } else {
            setSubmissionForm({
                briefDescription: '',
                links: [{ url: '' }]
            })
        }
        setShowSubmissionModal(true)
    }

    const handleViewSubmissionDetail = async (groupId) => {
        try {
            const data = await taskSubmissionAPI.getSubmissionByGroup(classId, taskId, groupId)
            setSelectedSubmission(data)
            setShowSubmissionDetailModal(true)
        } catch (err) {
            alert('Failed to load submission details')
        }
    }

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
                <main className="flex-1 overflow-y-auto">
                    <div className="max-w-4xl mx-auto px-6 py-6">
                        {/* Back button */}
                        <button
                            onClick={() => navigate(`/pbl-class/${classId}`)}
                            className="mb-4 flex items-center text-blue-600 hover:text-blue-700"
                        >
                            <svg className="w-5 h-5 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
                            </svg>
                            Back to Class
                        </button>

                        {/* Task Details */}
                        <div className="bg-white rounded-lg shadow p-6 mb-6">
                            <h1 className="text-2xl font-bold text-gray-900 mb-4">{task?.title}</h1>
                            <div className="prose max-w-none mb-4">
                                <p className="text-gray-700 whitespace-pre-wrap">{task?.description || 'No description'}</p>
                            </div>
                            <div className="text-sm text-gray-500">
                                <p>Due: {task?.dueDate ? new Date(task.dueDate).toLocaleString() : 'No due date'}</p>
                                <p>Created: {task?.createdAt ? new Date(task.createdAt).toLocaleString() : 'Unknown'}</p>
                            </div>
                        </div>

                        {/* Student Section */}
                        {isStudent && (
                            <div className="bg-white rounded-lg shadow p-6">
                                <div className="flex justify-between items-center mb-4">
                                    <h2 className="text-xl font-semibold text-gray-900">Your Group's Submission</h2>
                                    <button
                                        onClick={openSubmissionModal}
                                        className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
                                    >
                                        {mySubmission ? 'Update Submission' : 'Submit New'}
                                    </button>
                                </div>

                                {!mySubmission ? (
                                    <p className="text-gray-500 text-center py-8">Your group has not submitted anything yet.</p>
                                ) : (
                                    <div className="border-t pt-4">
                                        <p className="text-gray-700 mb-3">
                                            <span className="font-medium">Brief Description:</span><br />
                                            {mySubmission.briefDescription}
                                        </p>
                                        {mySubmission.links?.length > 0 && (
                                            <div>
                                                <span className="font-medium text-gray-700">Links:</span>
                                                <ul className="mt-2 space-y-1">
                                                    {mySubmission.links.map((link, idx) => (
                                                        <li key={idx}>
                                                            <a
                                                                href={link.url.startsWith('http') ? link.url : `https://${link.url}`}
                                                                target="_blank"
                                                                rel="noopener noreferrer"
                                                                className="text-blue-600 hover:underline break-all"
                                                            >
                                                                {link.url}
                                                            </a>
                                                        </li>
                                                    ))}
                                                </ul>
                                            </div>
                                        )}
                                        <div className="mt-4 text-sm text-gray-500">
                                            <p>Submitted: {mySubmission.submittedAt ? new Date(mySubmission.submittedAt).toLocaleString() : 'N/A'}</p>
                                            {mySubmission.isLate && (
                                                <p className="text-red-500 font-medium">Late Submission</p>
                                            )}
                                            <p>Status: {mySubmission.status}</p>
                                        </div>
                                    </div>
                                )}
                            </div>
                        )}

                        {/* Lecturer Section */}
                        {isLecturer && (
                            <div className="bg-white rounded-lg shadow p-6">
                                <h2 className="text-xl font-semibold text-gray-900 mb-4">All Submissions</h2>
                                {allSubmissions.length === 0 ? (
                                    <p className="text-gray-500 text-center py-8">No submissions yet.</p>
                                ) : (
                                    <div className="space-y-3">
                                        {allSubmissions.map((submission) => (
                                            <div
                                                key={submission.groupId}
                                                onClick={() => handleViewSubmissionDetail(submission.groupId)}
                                                className="border rounded-lg p-4 hover:shadow-md transition-shadow cursor-pointer"
                                            >
                                                <div className="flex justify-between items-center">
                                                    <div>
                                                        <h3 className="font-semibold text-gray-900">{submission.groupName}</h3>
                                                        <p className="text-sm text-gray-500">
                                                            Submitted by: {submission.submittedByStudentName || 'N/A'}
                                                        </p>
                                                    </div>
                                                    <div className="text-right">
                                                        {submission.hasSubmitted ? (
                                                            <>
                                                                <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${
                                                                    submission.isLate ? 'bg-red-100 text-red-800' : 'bg-green-100 text-green-800'
                                                                }`}>
                                                                    {submission.isLate ? 'Late' : 'Submitted'}
                                                                </span>
                                                                <p className="text-xs text-gray-500 mt-1">
                                                                    {submission.submittedAt ? new Date(submission.submittedAt).toLocaleDateString() : ''}
                                                                </p>
                                                            </>
                                                        ) : (
                                                            <span className="inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-gray-100 text-gray-600">
                                                                Not Submitted
                                                            </span>
                                                        )}
                                                    </div>
                                                </div>
                                            </div>
                                        ))}
                                    </div>
                                )}
                            </div>
                        )}
                    </div>
                </main>
            </div>

            {/* Submission Modal (Student) */}
            <Modal
                isOpen={showSubmissionModal}
                onClose={() => setShowSubmissionModal(false)}
                title={mySubmission ? 'Update Submission' : 'Submit New Submission'}
                size="lg"
            >
                <form onSubmit={handleSubmitSubmission} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Brief Description *</label>
                        <textarea
                            required
                            rows={4}
                            value={submissionForm.briefDescription}
                            onChange={(e) => setSubmissionForm({ ...submissionForm, briefDescription: e.target.value })}
                            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                            placeholder="Describe your group's work..."
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">Reference Links</label>
                        {submissionForm.links.map((link, index) => (
                            <div key={index} className="flex items-center space-x-2 mb-2">
                                <input
                                    type="url"
                                    value={link.url}
                                    onChange={(e) => handleLinkChange(index, e.target.value)}
                                    className="flex-1 px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                                    placeholder="https://..."
                                />
                                {submissionForm.links.length > 1 && (
                                    <button
                                        type="button"
                                        onClick={() => handleRemoveLink(index)}
                                        className="text-red-600 hover:text-red-800"
                                    >
                                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                        </svg>
                                    </button>
                                )}
                            </div>
                        ))}
                        <button
                            type="button"
                            onClick={handleAddLink}
                            className="mt-2 text-sm text-blue-600 hover:text-blue-800 flex items-center"
                        >
                            <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                            </svg>
                            Add another link
                        </button>
                    </div>

                    <div className="flex justify-end space-x-3 pt-4">
                        <button
                            type="button"
                            onClick={() => setShowSubmissionModal(false)}
                            className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            disabled={submitting}
                            className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 disabled:opacity-50"
                        >
                            {submitting ? 'Submitting...' : 'Submit'}
                        </button>
                    </div>
                </form>
            </Modal>

            {/* Submission Detail Modal (Lecturer) */}
            <Modal
                isOpen={showSubmissionDetailModal}
                onClose={() => setShowSubmissionDetailModal(false)}
                title={`Submission - ${selectedSubmission?.groupName || 'Group'}`}
                size="lg"
            >
                {selectedSubmission && (
                    <div className="space-y-4">
                        <div>
                            <h3 className="font-medium text-gray-900">Brief Description</h3>
                            <p className="text-gray-700 mt-1 whitespace-pre-wrap">{selectedSubmission.briefDescription}</p>
                        </div>

                        {selectedSubmission.links?.length > 0 && (
                            <div>
                                <h3 className="font-medium text-gray-900">Links</h3>
                                <ul className="mt-2 space-y-1">
                                    {selectedSubmission.links.map((link, idx) => (
                                        <li key={idx}>
                                            <a
                                                href={link.url.startsWith('http') ? link.url : `https://${link.url}`}
                                                target="_blank"
                                                rel="noopener noreferrer"
                                                className="text-blue-600 hover:underline break-all"
                                            >
                                                {link.url}
                                            </a>
                                        </li>
                                    ))}
                                </ul>
                            </div>
                        )}

                        <div className="border-t pt-4">
                            <div className="grid grid-cols-2 gap-2 text-sm">
                                <p className="text-gray-500">Submitted by:</p>
                                <p className="text-gray-900">{selectedSubmission.submittedBy?.fullName || 'N/A'}</p>

                                <p className="text-gray-500">Submitted at:</p>
                                <p className="text-gray-900">{selectedSubmission.submittedAt ? new Date(selectedSubmission.submittedAt).toLocaleString() : 'N/A'}</p>

                                <p className="text-gray-500">Last modified:</p>
                                <p className="text-gray-900">{selectedSubmission.lastModifiedAt ? new Date(selectedSubmission.lastModifiedAt).toLocaleString() : 'N/A'}</p>

                                <p className="text-gray-500">Status:</p>
                                <p className="text-gray-900">{selectedSubmission.status}</p>

                                {selectedSubmission.isLate && (
                                    <>
                                        <p className="text-gray-500">Late:</p>
                                        <p className="text-red-500 font-medium">Yes</p>
                                    </>
                                )}
                            </div>
                        </div>
                    </div>
                )}
            </Modal>
        </div>
    )
}