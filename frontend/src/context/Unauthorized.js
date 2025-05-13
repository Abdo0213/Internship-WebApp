import { FaBan } from 'react-icons/fa';
import { useNavigate } from 'react-router-dom'; // Import useNavigate hook

export default function Unauthorized() {
  const navigate = useNavigate(); // Initialize navigate function

  // Function to handle redirect to login page
    const handleGoToLogin = () => {
        navigate('/login'); // Navigate to login page
    };

    return (
        <div className="unauthorized-container">
        <div className="unauthorized-icon">
            <FaBan />
        </div>
        <h1>Access Denied</h1>
        <p>You don't have permission to view this page.</p>
        <p>Please contact your administrator if you believe this is an error.</p>

        {/* Button to redirect to login */}
        <button className="btn-go-to-login" onClick={handleGoToLogin}>
            Go to Login
        </button>
        </div>
    );
}