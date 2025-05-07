// components/Login.js
import { useState, useEffect } from 'react';
import { useNavigate, Link, useLocation } from 'react-router-dom'; // Added useLocation
import axios from 'axios';
import { useAuth } from '../../context/authcontext';
import "./Login.css"

export default function Login() {
  const [formData, setFormData] = useState({
    username: '',
    password: ''
  });
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();
  const location = useLocation(); // Proper way to access location
  const { login, isAuthenticated } = useAuth();

  // Redirect if already logged in
  useEffect(() => {
    if (isAuthenticated ) {
      const userData = JSON.parse(localStorage.getItem('userData'));
        const role = userData.roles?.[0];
        // Navigate based on role
        if (role === 'editor') {
            navigate('/editor');
        } else {
            navigate('/dashboard');
          }
    }
  }, [isAuthenticated, navigate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };
  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    try {
      const response = await axios.post('http://localhost:8765/user-service/auth/login', formData, {
        validateStatus: (status) => status < 500
      });
      if (response.status === 200) {
        login(response.data);
        // Use location from useLocation hook instead of global location
        const from = location.state?.from?.pathname;
        navigate(from, { replace: true });
      } else {
        setError(response.data?.error || 'Login failed. Please try again.');
      }
    } catch (error) {
      console.error('Login error:', error);
      setError('An unexpected error occurred. Please try again later.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h2 className="auth-title">Login to Your Account</h2>
        
        {error && (
          <div className="auth-error">
            <p>{error}</p>
            {error.includes('invalid') && (
              <p className="auth-error-hint">
                Forgot your password? <Link to="/reset-password">Reset it here</Link>
              </p>
            )}
          </div>
        )}
        
        <form onSubmit={handleLogin} className="auth-form">
          <div className="form-group">
            <label htmlFor="username" className="auth-label">Username</label>
            <input
              id="username"
              type="text"
              name="username"
              value={formData.username}
              onChange={handleChange}
              required
              className="auth-input full-width"
              placeholder="Enter your username"
              autoComplete="username"
              disabled={isLoading}
            />
          </div>
          
          <div className="form-group">
            <label htmlFor="password" className="auth-label">Password</label>
            <input
              id="password"
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              required
              className="auth-input full-width"
              placeholder="Enter your password"
              autoComplete="current-password"
              disabled={isLoading}
            />
          </div>
          
          <button 
            type="submit" 
            className="auth-button primary full-width"
            disabled={isLoading}
          >
            {isLoading ? (
              <span className="auth-button-loading">Logging in...</span>
            ) : (
              'Login'
            )}
          </button>
        </form>
        
        <div className="auth-footer">
          <div className="auth-footer-links">
            <p>Don't have an account? <Link to="/register" className="auth-link-button">Sign Up</Link></p>
            <p><Link to="/forgot-password" className="auth-link-button">Forgot password?</Link></p>
          </div>
        </div>
      </div>
    </div>
  );
}