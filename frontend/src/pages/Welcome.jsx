import { useNavigate } from 'react-router-dom'
import backgroundImage from '../assets/image007.jpg'

export default function Welcome() {
    const navigate = useNavigate()

    return (
        <div className="relative min-h-screen bg-white flex flex-col">
            {/* Background Image with Blur */}
            <div
                className="absolute inset-0 bg-cover bg-center bg-no-repeat"
                style={{
                    backgroundImage: `url(${backgroundImage})`,
                    filter: 'blur(4px)',
                    transform: 'scale(1.05)' // Prevents blur edges from showing
                }}
            />

            {/* Dark overlay for better text readability */}
            <div className="absolute inset-0 bg-black/30" />

            {/* Top bar with Login button */}
            <div className="relative z-10 flex justify-end p-6">
                <button
                    onClick={() => navigate('/login')}
                    className="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded-lg transition-colors font-medium shadow-lg"
                >
                    Login
                </button>
            </div>

            {/* Center content */}
            <div className="relative z-10 flex-1 flex items-center justify-center">
                <div className="text-center max-w-2xl px-4">
                    <h1 className="text-5xl font-bold text-white mb-4 drop-shadow-lg">
                        Welcome to PBL Management System
                    </h1>
                    <p className="text-xl text-white/90 drop-shadow-md">
                        DUT's dedicated digital hub for Project-Based Learning. This centralized platform simplifies how instructors manage PBL classrooms, assign coursework, and collaborate with teams, while providing transparent tracking of student milestones and academic growth..
                    </p>
                </div>
            </div>

            {/* Footer with copyright */}
            <footer className="relative z-10 text-center py-6 text-white/70 text-sm">
                © {new Date().getFullYear()} PBL Management System. All rights reserved.
            </footer>
        </div>
    )
}