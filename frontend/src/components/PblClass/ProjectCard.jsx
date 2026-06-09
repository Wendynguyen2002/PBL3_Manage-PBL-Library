export default function ProjectCard({
                                        project,
                                        isLecturer,
                                        onViewDetails,
                                        onEdit,
                                        onDelete,
                                        setDeleteProjectConfirm,
                                        deleteProjectConfirm
                                    }) {
    const isTaken = project.projectStatus === 'TAKEN'

    const handleDeleteClick = () => {
        setDeleteProjectConfirm({ id: project.id, title: project.title })
    }

    return (
        <div className={`bg-white rounded-lg shadow-sm border p-4 hover:shadow-md transition-shadow ${isTaken ? 'border-yellow-200 bg-yellow-50/30' : 'border-gray-200'}`}>
            <div className="flex justify-between items-start">
                <div className="flex-1">
                    <div className="flex items-center space-x-3">
                        <h3 className="text-md font-semibold text-gray-900">{project.title}</h3>
                        {isTaken && (
                            <span className="inline-flex px-2 py-0.5 text-xs font-medium rounded-full bg-yellow-100 text-yellow-800">
                                Taken
                            </span>
                        )}
                    </div>
                </div>
                <div className="flex space-x-2">
                    {/* View details button */}
                    <button
                        onClick={() => onViewDetails(project)}
                        className="text-blue-600 hover:text-blue-800"
                        title="View details"
                    >
                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </svg>
                    </button>

                    {/* Edit button (Lecturer only) */}
                    {isLecturer && (
                        <button
                            onClick={() => onEdit(project)}
                            className="text-green-600 hover:text-green-800"
                            title="Edit project"
                        >
                            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                            </svg>
                        </button>
                    )}

                    {/* Delete button (Lecturer only) */}
                    {isLecturer && (
                        <button
                            onClick={handleDeleteClick}
                            className="text-red-600 hover:text-red-800"
                            title="Delete project"
                        >
                            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                            </svg>
                        </button>
                    )}
                </div>
            </div>
        </div>
    )
}