import { Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './contexts/AuthContext'
import ProtectedRoute from './components/ProtectedRoute'
import Welcome from './pages/Welcome'
import Login from './pages/Login'
import Dashboard from './pages/Dashboard'
import Profile from './pages/Profile'
import SchoolManagement from './pages/admin/SchoolManagement'
import AccountManagement from './pages/admin/AccountManagement'
import PblClassPage from './pages/PblClassPage'
import TaskPage from './pages/TaskPage'
import Library from './pages/Library'

function App() {
    return (
        <AuthProvider>
            <Routes>
                <Route path="/" element={<Welcome />} />
                <Route path="/login" element={<Login />} />
                <Route
                    path="/dashboard"
                    element={
                        <ProtectedRoute>
                            <Dashboard />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/library"
                    element={
                        <ProtectedRoute>
                            <Library />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/pbl-class/:classId/task/:taskId"
                    element={
                        <ProtectedRoute>
                            <TaskPage />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/profile"
                    element={
                        <ProtectedRoute>
                            <Profile />
                        </ProtectedRoute>
                    }
                />
                {/* Admin Routes - Your existing working routes */}
                <Route
                    path="/admin/school-management"
                    element={
                        <ProtectedRoute>
                            <SchoolManagement />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/admin/account-management"
                    element={
                        <ProtectedRoute>
                            <AccountManagement />
                        </ProtectedRoute>
                    }
                />
                {/* PBL Class Page - New route for viewing class details */}
                <Route
                    path="/pbl-class/:classId"
                    element={
                        <ProtectedRoute>
                            <PblClassPage />
                        </ProtectedRoute>
                    }
                />
                {/* These placeholder routes are for future implementation */}
                <Route
                    path="/lecturer/classes"
                    element={
                        <ProtectedRoute>
                            <div className="flex h-screen items-center justify-center">
                                <div className="text-center">
                                    <h1 className="text-2xl font-bold">Lecturer Classes</h1>
                                    <p className="mt-2 text-gray-600">Coming soon...</p>
                                </div>
                            </div>
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/lecturer/assignments"
                    element={
                        <ProtectedRoute>
                            <div className="flex h-screen items-center justify-center">
                                <div className="text-center">
                                    <h1 className="text-2xl font-bold">Lecturer Assignments</h1>
                                    <p className="mt-2 text-gray-600">Coming soon...</p>
                                </div>
                            </div>
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/student/classes"
                    element={
                        <ProtectedRoute>
                            <div className="flex h-screen items-center justify-center">
                                <div className="text-center">
                                    <h1 className="text-2xl font-bold">Student Classes</h1>
                                    <p className="mt-2 text-gray-600">Coming soon...</p>
                                </div>
                            </div>
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/student/submissions"
                    element={
                        <ProtectedRoute>
                            <div className="flex h-screen items-center justify-center">
                                <div className="text-center">
                                    <h1 className="text-2xl font-bold">Student Submissions</h1>
                                    <p className="mt-2 text-gray-600">Coming soon...</p>
                                </div>
                            </div>
                        </ProtectedRoute>
                    }
                />
                <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
        </AuthProvider>
    )
}

export default App