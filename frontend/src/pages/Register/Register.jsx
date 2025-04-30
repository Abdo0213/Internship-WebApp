// components/Register.js
import { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';


export default function Register() {
  const [formData, setFormData] = useState({
    username: '',
    password: '',
    roles: ['user'] // Default role
  });
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    let isTokenValid = false;
    if (token) {
      try {
        const decoded = jwtDecode(token);
        isTokenValid = decoded.exp * 1000 > Date.now();
      } catch (err) {
        console.error("Invalid or corrupt token", err);
        isTokenValid = false;
      }
    }
    if (token && isTokenValid) {
        const userData = JSON.parse(localStorage.getItem('userData'));
        const role = userData.roles?.[0];
        // Navigate based on role
        if (role === 'editor') {
            navigate('/editor');
        } else {
            navigate('/dashboard');
          }
    }
  }, [navigate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };
  const [selectedRole, setSelectedRole] = useState('user'); // Default role
  const handleRoleChange = (e) => {
    setSelectedRole(e.target.value);
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setError('');
    /*try {
      await axios.post('http://localhost:5000/register', formData);
      alert('Registration successful! Please login.');
      navigate('/login');
    } catch (error) {
      setError(error.response?.data || 'Registration failed. Please try again.');
    }*/
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h2 className="auth-title">Create Account</h2>
        
        {error && <div className="auth-error">{error}</div>}
        
        <form onSubmit={handleRegister} className="auth-form">
          <div className="form-group">
            <input
              type="text"
              name="username"
              value={formData.username}
              onChange={handleChange}
              required
              className="auth-input full-width"
              placeholder="Choose a username"
            />
          </div>
          
          <div className="form-group">
            <input
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              required
              className="auth-input full-width"
              placeholder="Create a password"
            />
          </div>

          <div className="form-group">
            <label className="roles-label">Select Roles:</label>
            <div className="roles-options">
              <label className="role-option">
                <input
                  type="radio"
                  value="user"
                  checked={selectedRole === 'user'}
                  onChange={handleRoleChange}
                />
                <span className="role-badge user">User</span>
              </label>
              
              <label className="role-option">
                <input
                  type="radio"
                  value="editor"
                  checked={selectedRole === 'editor'}
                  onChange={handleRoleChange}
                />
                <span className="role-badge editor">Editor</span>
              </label>
            </div>
          </div>
          
          <button type="submit" className="auth-button primary full-width">
            Sign Up
          </button>
        </form>
        
        <div className="auth-footer">
          <p>Already have an account?</p>
          <Link to="/login" className="auth-link-button">
            Login
          </Link>
        </div>
      </div>
    </div>
  );
}