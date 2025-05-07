import { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import "./Register.css"

export default function Register() {
  const [formData, setFormData] = useState({
    username: '',
    password: '',
    email: '',
    fname: '',
    cv: '' // New field for the CV filename
  });
  const [error, setError] = useState('');
  const [fileError, setFileError] = useState('');
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
      const decoded = jwtDecode(token);
      const role = decoded.role;
      // Navigate based on role
      if (role === 'ADMIN') {
        navigate('/admin');
      } else if (role === 'HR'){
        navigate('/hr');
      } else {
        navigate('/');
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

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    setFileError('');
    
    if (!file) {
      setFormData(prev => ({ ...prev, cv: '' }));
      return;
    }

    // Validate file type
    if (file.type !== 'application/pdf') {
      setFileError('Please upload a PDF file only');
      return;
    }

    // Validate file size (e.g., 5MB max)
    if (file.size > 5 * 1024 * 1024) {
      setFileError('File size should be less than 5MB');
      return;
    }

    // Store just the filename in formData
    setFormData(prev => ({
      ...prev,
      cv: file.name
    }));
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setError('');
    
    // Validate CV was uploaded if required
    if (!formData.cv) {
      setFileError('CV is required');
      return;
    }
    console.log(formData);

    try {
      await axios.post(
        'http://localhost:8765/user-service/auth/register',
        formData,
        {
          headers: {
            'Content-Type': 'application/json',
          },
          withCredentials: true
        }
      );
      alert('Registration successful! Please login.');
      navigate('/login');
    } catch (error) {
      console.error('Registration error:', error);
      setError(error.response?.data?.message || 
            'Registration failed. Please try again.');
    }
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
            name="fName"
            value={formData.fName}
            onChange={handleChange}
            required
            className="auth-input full-width"
            placeholder="Enter Full Name"
          />
        </div>
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
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              required
              className="auth-input full-width"
              placeholder="Enter your email"
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
              
              <div className="file-upload">
                  <input
                  type="file"
                  id="cvUpload"
                  accept=".pdf"
                  onChange={handleFileChange}
                  required
                  />
                  <div className="file-upload-label">
                  {formData.cv != '' ? formData.cv : 'Choose file...'}
                  </div>
              </div>
            </div>
            {fileError && <div className="auth-error">{fileError}</div>}

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