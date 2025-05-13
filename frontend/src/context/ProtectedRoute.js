// components/ProtectedRoute.js
import { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import Unauthorized from './Unauthorized';
import { jwtDecode } from 'jwt-decode';

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

      try {
        const decoded = jwtDecode(token);
        const userRole = decoded.role; // Now a string instead of array
        const isTokenValid = decoded.exp * 1000 > Date.now();

        console.log('Auth verification:', {
          path: location.pathname,
          userRole,
          requiredRoles
        });

        // Admin users have access to everything
        /*
        if (userRole === 'ADMIN') {
          setAuthState({
            isAuthorized: true,
            isAdmin: true
          });
          return;
        }*/

        // For non-admin users, check required roles
        const isAuthorized = isTokenValid && 
          (!requiredRoles || requiredRoles.includes(userRole));

        setAuthState({
          isAuthorized,
          isAdmin: false
        });

        if (!isAuthorized) {
          console.warn('Unauthorized access attempt:', {
            path: location.pathname,
            userRole,
            requiredRoles
          });
        }
      } catch (err) {
        console.error("Invalid or corrupt token", err);
        navigate('/login', { 
          state: { 
            from: location.pathname,
            message: 'Session expired. Please login again'
          } 
        });
        setAuthState({ isAuthorized: false, isAdmin: false });
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