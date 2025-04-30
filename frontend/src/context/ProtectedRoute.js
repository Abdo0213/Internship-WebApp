// components/ProtectedRoute.js
import { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import Unauthorized from './UnAuthorized';
import '../assets/Auth.css';
import { jwtDecode } from 'jwt-decode';
import axios from 'axios';


export default function ProtectedRoute({ children, requiredRoles }) {
  const [authState, setAuthState] = useState({
    isAuthorized: null,
    isAdmin: false
  });
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    const verifyAuth = () => {
      const token = localStorage.getItem('accessToken');
      const userData = JSON.parse(localStorage.getItem('userData') || '{}');
      const userRoles = userData?.roles || [];
      console.log('Auth verification:', {
        path: location.pathname,
        userRoles,
        requiredRoles
      });

      if (!token) {
        navigate('/login', { 
          state: { 
            from: location.pathname,
            message: 'Please login to access this page'
          } 
        });
        setAuthState({ isAuthorized: false, isAdmin: false });
        return;
      }

      // Admin users should have access to everything
      if (userRoles.includes('admin')) {
        setAuthState({
          isAuthorized: true,
          isAdmin: true
        });
        return;
      }

      // For non-admin users, check required roles
      const token2 = localStorage.getItem('accessToken');
      let isTokenValid = false;
      if (token2) {
        try {
          const decoded = jwtDecode(token2);
          isTokenValid = (decoded.exp) * 1000 > Date.now();
          console.log(decoded.exp );
          console.log(Date.now());
        } catch (err) {
          console.error("Invalid or corrupt token", err);
          isTokenValid = false;
        }
      }
      const isAuthorized = isTokenValid && (!requiredRoles || requiredRoles.some(role => userRoles.includes(role)));
      setAuthState({
        isAuthorized,
        isAdmin: false
      });

      if (!isAuthorized) {
        console.warn('Unauthorized access attempt:', {
          path: location.pathname,
          userRoles,
          requiredRoles
        });
      }
    };

    verifyAuth();
  }, [navigate, location, requiredRoles]);

  // Render states
  if (authState.isAuthorized === null) {
    return <div className="auth-loading">Verifying permissions...</div>;
  }
  if (authState.isAuthorized === false) {
    return localStorage.getItem('accessToken') ? <Unauthorized /> : null;
  }

  return children;
}

