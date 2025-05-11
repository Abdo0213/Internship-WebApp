// components/Login.js
import { useState, useEffect } from 'react';
import { useNavigate, Link, useLocation } from 'react-router-dom'; // Added useLocation
import axios from 'axios';
import { useAuth } from '../../context/authcontext';
import style from "./Login.module.css"
import { jwtDecode } from 'jwt-decode';

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
      const role = jwtDecode(localStorage.getItem("accessToken")).role;
      console.log(role);
      
      // Navigate based on role
      if (role === 'AMDIN') {
          navigate('/admin');
      } else if (role === "HR"){
          navigate('/hr');
      } else {
        navigate('/');
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
    <div className={style["auth-container"]}>
      <div className={style["auth-card"]}>
        <h2 className={style["auth-title"]}>Login to Your Account</h2>
        
        {error && (
          <div className={style["auth-error"]}>
            <p>{error}</p>
            {error.includes('invalid') && (
              <p className={style["auth-error-hint"]}>
                Forgot your password? <Link to="/reset-password">Reset it here</Link>
              </p>
            )}
          </div>
        )}
        
        <form onSubmit={handleLogin} className={style["auth-form"]}>
          <div className={style["form-group"]}>
            <label htmlFor="username" className={style["auth-label"]}>Username</label>
            <input
              id="username"
              type="text"
              name="username"
              value={formData.username}
              onChange={handleChange}
              required
              className={`${style['auth-input']} ${style['full-width']}`}
              placeholder="Enter your username"
              autoComplete="username"
              disabled={isLoading}
            />
          </div>
          
          <div className={style["form-group"]}>
            <label htmlFor="password" className={style["auth-label"]}>Password</label>
            <input
              id="password"
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              required
              className={`${style['auth-input']} ${style['full-width']}`}
              placeholder="Enter your password"
              autoComplete="current-password"
              disabled={isLoading}
            />
          </div>
          
          <button 
            type="submit" 
            className={`${style['auth-button']} ${style['primary']} ${style['full-width']}`}
            disabled={isLoading}
          >
            {isLoading ? (
              <span className={style["auth-button-loading"]}>Logging in...</span>
            ) : (
              'Login'
            )}
          </button>
        </form>
        
        <div className={style["auth-footer"]}>
          <div className={style["auth-footer-links"]}>
            <p>Don't have an account? <Link to="/register" className={style["auth-link-button"]}>Sign Up</Link></p>
            <p><Link to="/forgot-password" className={style["auth-link-button"]}>Forgot password?</Link></p>
          </div>
        </div>
      </div>
    </div>
  );
}