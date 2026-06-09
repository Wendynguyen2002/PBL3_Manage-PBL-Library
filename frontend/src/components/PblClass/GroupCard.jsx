import { useState } from 'react'

export default function GroupCard({
                                      group,
                                      currentUser,
                                      isLecturer,
                                      isStudent,
                                      isUserInGroup,
                                      currentUserGroupId,
                                      maxStudentsPerGroup,
                                      projects,
                                      onJoinGroup,
                                      onDisbandGroup,
                                      onDeleteGroup,
                                      onRemoveStudentFromGroup,
                                      onOpenGroupProjectModal,
                                      setDeleteGroupConfirm,
                                      setRemoveGroupStudentConfirm,
                                      deleteGroupConfirm,
                                      removeGroupStudentConfirm
                                  }) {
    const isFull = group.members?.length >= maxStudentsPerGroup
    const isCurrentUserGroup = currentUserGroupId === group.id
    const canDisband = isStudent && isCurrentUserGroup && group.members?.length === 1

    const handleDeleteGroupClick = () => {
        setDeleteGroupConfirm({ id: group.id, name: group.groupName || `Group ${group.id}` })
    }

    const handleRemoveStudent = (studentId) => {
        setRemoveGroupStudentConfirm({ groupId: group.id, studentId })
    }

    return (
        <div className="bg-white rounded-lg shadow-md border border-gray-200 overflow-hidden hover:shadow-lg transition-shadow">
            <div className="p-4">
                <div className="flex justify-between items-start mb-3">
                    <h3 className="text-lg font-semibold text-gray-900">
                        {group.groupName || `Group ${group.id}`}
                    </h3>
                    <div className="flex space-x-2">
                        {isStudent && isCurrentUserGroup && group.projectTitle && (
                            <button
                                onClick={() => onOpenGroupProjectModal(group)}
                                className="text-purple-600 hover:text-purple-800"
                                title="Change project"
                            >
                                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                                </svg>
                            </button>
                        )}
                        {isStudent && isCurrentUserGroup && !group.projectTitle && (
                            <button
                                onClick={() => onOpenGroupProjectModal(group)}
                                className="text-green-600 hover:text-green-800"
                                title="Select project"
                            >
                                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                                </svg>
                            </button>
                        )}
                        {canDisband && (
                            <button
                                onClick={() => onDisbandGroup(group.id)}
                                className="text-red-600 hover:text-red-800"
                                title="Disband group"
                            >
                                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                </svg>
                            </button>
                        )}
                        {isLecturer && (
                            <button
                                onClick={handleDeleteGroupClick}
                                className="text-red-600 hover:text-red-800"
                                title="Delete group"
                            >
                                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                </svg>
                            </button>
                        )}
                    </div>
                </div>

                {/* Project info */}
                {group.projectTitle && (
                    <div className="mb-3 p-2 bg-blue-50 rounded-md">
                        <p className="text-sm text-blue-800">
                            <span className="font-medium">Project:</span> {group.projectTitle}
                        </p>
                    </div>
                )}

                {/* Members list */}
                <div className="mb-3">
                    <p className="text-sm text-gray-600 mb-2">
                        Members ({group.members?.length || 0}/{maxStudentsPerGroup}):
                    </p>
                    <div className="space-y-2">
                        {group.members?.map(member => (
                            <div key={member.id} className="flex justify-between items-center">
                                <div>
                                    <p className="text-sm text-gray-900">{member.fullName}</p>
                                    {member.id === currentUser?.id && (
                                        <span className="text-xs text-blue-600">(You)</span>
                                    )}
                                </div>
                                {isLecturer && (
                                    <button
                                        onClick={() => handleRemoveStudent(member.id)}
                                        className="text-red-500 hover:text-red-700 text-sm"
                                        title="Remove student from group"
                                    >
                                        <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                        </svg>
                                    </button>
                                )}
                            </div>
                        ))}
                    </div>
                </div>

                {/* Join button for students */}
                {isStudent && !isUserInGroup && !isFull && (
                    <button
                        onClick={() => onJoinGroup(group.id)}
                        className="w-full mt-2 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
                    >
                        Join Group
                    </button>
                )}

                {isStudent && !isUserInGroup && isFull && (
                    <button
                        disabled
                        className="w-full mt-2 py-2 bg-gray-300 text-gray-500 rounded-md cursor-not-allowed"
                        title="Group is full"
                    >
                        Group Full
                    </button>
                )}

                {isStudent && isCurrentUserGroup && (
                    <div className="mt-2 text-center text-sm text-green-600">
                        You are a member of this group
                    </div>
                )}
            </div>
        </div>
    )
}