import { useState, useEffect, useCallback } from 'react'
import { useAuth } from '../hooks/useAuth'
import Sidebar from '../components/Layout/Sidebar'
import Navbar from '../components/Layout/Navbar'
import LoadingSpinner from '../components/common/LoadingSpinner'
import Modal from '../components/common/Modal'
import { libraryAPI } from '../api/libraryAPI'
import { departmentAPI } from '../api/departmentAPI'

export default function Library() {
    const { user } = useAuth()
    const [reports, setReports] = useState([])
    const [loading, setLoading] = useState(true)
    const [totalPages, setTotalPages] = useState(0)
    const [currentPage, setCurrentPage] = useState(0)

    // Search filters
    const [keyword, setKeyword] = useState('')
    const [className, setClassName] = useState('')
    const [departments, setDepartments] = useState([])
    const [selectedDepartmentId, setSelectedDepartmentId] = useState('')
    const [fileType, setFileType] = useState('')
    const [sortBy, setSortBy] = useState('newest')

    // Report detail modal
    const [selectedReport, setSelectedReport] = useState(null)
    const [showDetailModal, setShowDetailModal] = useState(false)
    const [userRating, setUserRating] = useState(null)
    const [ratingSubmitting, setRatingSubmitting] = useState(false)

    // Debounced search
    const [debouncedKeyword, setDebouncedKeyword] = useState('')

    useEffect(() => {
        console.log('Library component mounted, fetching departments and reports')
        fetchDepartments()
        searchReports()
    }, [])

    useEffect(() => {
        const timer = setTimeout(() => {
            setDebouncedKeyword(keyword)
        }, 500)
        return () => clearTimeout(timer)
    }, [keyword])

    useEffect(() => {
        if (debouncedKeyword !== undefined) {
            setCurrentPage(0)
            searchReports()
        }
    }, [debouncedKeyword, className, selectedDepartmentId, fileType, sortBy])

    useEffect(() => {
        searchReports()
    }, [currentPage])

    const fetchDepartments = async () => {
        try {
            const depts = await departmentAPI.getDepartmentsForDropdown()
            setDepartments(depts)
        } catch (err) {
            console.error('Failed to fetch departments:', err)
        }
    }

    const searchReports = async () => {
        setLoading(true)
        try {
            const data = await libraryAPI.searchReports({
                keyword: debouncedKeyword,
                className: className,
                departmentId: selectedDepartmentId,
                fileType: fileType,
                sortBy: sortBy,
                page: currentPage,
                size: 12
            })
            console.log('=== SEARCH RESPONSE ===')
            console.log('Full response:', data)
            console.log('Content:', data.content)
            console.log('Total pages:', data.totalPages)
            console.log('Total elements:', data.totalElements)
            console.log('Current page:', data.number)
            setReports(data.content)
            setTotalPages(data.totalPages)
        } catch (err) {
            console.error('Failed to search reports:', err)
        } finally {
            setLoading(false)
        }
    }

    const handleViewReport = async (reportId) => {
        try {
            const report = await libraryAPI.getReportDetails(reportId)
            setSelectedReport(report)
            // Get user's rating
            const rating = await libraryAPI.getUserRating(reportId)
            setUserRating(rating)
            setShowDetailModal(true)
        } catch (err) {
            alert('Failed to load report details')
        }
    }

    const handleDownload = async (reportId, title) => {
        try {
            const response = await libraryAPI.downloadReport(reportId)
            const filename = `${title.replace(/\s+/g, '_')}.${response.headers['content-type']?.split('/')[1] || 'pdf'}`
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

    const handleRate = async (rating) => {
        if (!user) {
            alert('Please login to rate reports')
            return
        }
        setRatingSubmitting(true)
        try {
            await libraryAPI.rateReport(selectedReport.id, rating)
            setUserRating(rating)
            // Refresh report details to update average rating
            const updated = await libraryAPI.getReportDetails(selectedReport.id)
            setSelectedReport(updated)
            alert('Thank you for rating!')
        } catch (err) {
            alert(err.response?.data?.message || 'Failed to submit rating')
        } finally {
            setRatingSubmitting(false)
        }
    }

    const renderStars = (rating, interactive = false, onRate = null) => {
        return (
            <div className="flex items-center">
                {[1, 2, 3, 4, 5].map(star => (
                    <button
                        key={star}
                        type="button"
                        onClick={() => interactive && onRate && onRate(star)}
                        disabled={!interactive || ratingSubmitting}
                        className={`${interactive ? 'cursor-pointer' : 'cursor-default'} ${star <= (rating || 0) ? 'text-yellow-400' : 'text-gray-300'}`}
                    >
                        <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                            <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                        </svg>
                    </button>
                ))}
            </div>
        )
    }

    const getFileTypeIcon = (fileType) => {
        switch (fileType) {
            case 'PDF': return '📄'
            case 'DOCX': return '📝'
            case 'PPT': return '📊'
            default: return '📁'
        }
    }

    return (
        <div className="flex h-screen bg-gray-50">
            <Sidebar />
            <div className="flex-1 flex flex-col overflow-hidden">
                <Navbar />
                <main className="flex-1 overflow-y-auto">
                    <div className="max-w-7xl mx-auto px-6 py-6">
                        <h1 className="text-2xl font-bold text-gray-900 mb-2">Library</h1>
                        <p className="text-gray-600 mb-6">Browse and download public final reports from PBL classes</p>

                        {/* Filters */}
                        <div className="bg-white rounded-lg shadow p-4 mb-6">
                            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4">
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Search</label>
                                    <input
                                        type="text"
                                        value={keyword}
                                        onChange={(e) => setKeyword(e.target.value)}
                                        placeholder="Search by title..."
                                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Class Name</label>
                                    <input
                                        type="text"
                                        value={className}
                                        onChange={(e) => setClassName(e.target.value)}
                                        placeholder="Filter by class..."
                                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Department</label>
                                    <select
                                        value={selectedDepartmentId}
                                        onChange={(e) => setSelectedDepartmentId(e.target.value)}
                                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                                    >
                                        <option value="">All Departments</option>
                                        {departments.map(dept => (
                                            <option key={dept.id} value={dept.id}>{dept.name}</option>
                                        ))}
                                    </select>
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">File Type</label>
                                    <select
                                        value={fileType}
                                        onChange={(e) => setFileType(e.target.value)}
                                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                                    >
                                        <option value="">All Types</option>
                                        <option value="PDF">PDF</option>
                                        <option value="DOCX">DOCX</option>
                                        <option value="PPT">PPT/PPTX</option>
                                    </select>
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Sort By</label>
                                    <select
                                        value={sortBy}
                                        onChange={(e) => setSortBy(e.target.value)}
                                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                                    >
                                        <option value="newest">Newest First</option>
                                        <option value="highest_rated">Highest Rated</option>
                                        <option value="most_downloaded">Most Downloaded</option>
                                    </select>
                                </div>
                            </div>
                        </div>

                        {/* Results */}
                        {loading ? (
                            <div className="flex justify-center py-12">
                                <LoadingSpinner />
                            </div>
                        ) : reports.length === 0 ? (
                            <div className="bg-white rounded-lg shadow p-12 text-center">
                                <svg className="w-16 h-16 text-gray-400 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
                                </svg>
                                <p className="text-gray-500">No reports found</p>
                                <p className="text-sm text-gray-400 mt-2">Try adjusting your search filters</p>
                            </div>
                        ) : (
                            <>
                                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                                    {reports.map(report => (
                                        <div
                                            key={report.id}
                                            onClick={() => handleViewReport(report.id)}
                                            className="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow cursor-pointer overflow-hidden"
                                        >
                                            <div className="p-5">
                                                <div className="flex items-start justify-between mb-3">
                                                    <div className="text-3xl">{getFileTypeIcon(report.fileType)}</div>
                                                    <div className="flex items-center">
                                                        {renderStars(report.averageRating || 0)}
                                                        <span className="text-xs text-gray-500 ml-1">({report.ratingCount || 0})</span>
                                                    </div>
                                                </div>
                                                <h3 className="font-semibold text-gray-900 mb-1 line-clamp-2">{report.title}</h3>
                                                <p className="text-sm text-gray-500 line-clamp-2 mb-3">{report.description || 'No description'}</p>
                                                <div className="text-xs text-gray-400">
                                                    <p className="mb-1">{report.className}</p>
                                                    <p>{report.departmentName}</p>
                                                    <p className="mt-2">
                                                        <svg className="w-4 h-4 inline mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                                                        </svg>
                                                        {report.downloadCount || 0} downloads
                                                    </p>
                                                </div>
                                            </div>
                                        </div>
                                    ))}
                                </div>

                                {/* Pagination */}
                                {totalPages > 1 && (
                                    <div className="flex justify-center mt-8">
                                        <nav className="flex space-x-2">
                                            <button
                                                onClick={() => setCurrentPage(p => Math.max(0, p - 1))}
                                                disabled={currentPage === 0}
                                                className="px-3 py-2 rounded-md border border-gray-300 text-sm font-medium disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                                            >
                                                Previous
                                            </button>
                                            {[...Array(totalPages)].map((_, i) => (
                                                <button
                                                    key={i}
                                                    onClick={() => setCurrentPage(i)}
                                                    className={`px-3 py-2 rounded-md text-sm font-medium ${
                                                        currentPage === i
                                                            ? 'bg-blue-600 text-white'
                                                            : 'border border-gray-300 hover:bg-gray-50'
                                                    }`}
                                                >
                                                    {i + 1}
                                                </button>
                                            ))}
                                            <button
                                                onClick={() => setCurrentPage(p => Math.min(totalPages - 1, p + 1))}
                                                disabled={currentPage === totalPages - 1}
                                                className="px-3 py-2 rounded-md border border-gray-300 text-sm font-medium disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                                            >
                                                Next
                                            </button>
                                        </nav>
                                    </div>
                                )}
                            </>
                        )}
                    </div>
                </main>
            </div>

            {/* Report Detail Modal */}
            <Modal
                isOpen={showDetailModal}
                onClose={() => setShowDetailModal(false)}
                title="Report Details"
                size="lg"
            >
                {selectedReport && (
                    <div className="space-y-4">
                        <div>
                            <h3 className="text-xl font-bold text-gray-900">{selectedReport.title}</h3>
                            <p className="text-gray-700 mt-2 whitespace-pre-wrap">{selectedReport.description || 'No description'}</p>
                        </div>

                        <div className="border-t pt-4">
                            <div className="flex justify-between items-center mb-3">
                                <div>
                                    <div className="flex items-center">
                                        {renderStars(selectedReport.averageRating || 0)}
                                        <span className="text-sm text-gray-600 ml-2">
                                            ({selectedReport.ratingCount || 0} ratings)
                                        </span>
                                    </div>
                                </div>
                                <div className="flex items-center space-x-4">
                                    <span className="text-sm text-gray-500">
                                        📥 {selectedReport.downloadCount || 0} downloads
                                    </span>
                                    <span className="text-sm text-gray-500">
                                        📅 {new Date(selectedReport.submittedAt).toLocaleDateString()}
                                    </span>
                                </div>
                            </div>

                            <div className="grid grid-cols-2 gap-2 text-sm mb-4">
                                <p className="text-gray-500">Class:</p>
                                <p className="text-gray-900">{selectedReport.className}</p>
                                <p className="text-gray-500">Department:</p>
                                <p className="text-gray-900">{selectedReport.departmentName}</p>
                                <p className="text-gray-500">File Type:</p>
                                <p className="text-gray-900">{selectedReport.fileType}</p>
                            </div>

                            <div className="border-t pt-4">
                                <p className="text-sm font-medium text-gray-700 mb-2">Rate this report:</p>
                                <div className="flex items-center space-x-2">
                                    {renderStars(userRating || 0, true, handleRate)}
                                    {userRating && (
                                        <span className="text-xs text-gray-500">(Your rating: {userRating} stars)</span>
                                    )}
                                </div>
                            </div>

                            <div className="pt-4">
                                <button
                                    onClick={() => handleDownload(selectedReport.id, selectedReport.title)}
                                    className="w-full px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700"
                                >
                                    Download Report
                                </button>
                            </div>
                        </div>
                    </div>
                )}
            </Modal>
        </div>
    )
}