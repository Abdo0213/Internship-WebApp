// components/Logout.js
import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

export default function Logout() {
  const navigate = useNavigate();

  useEffect(() => {
    console.log("I ama here")
    localStorage.removeItem('accessToken');
    localStorage.removeItem('userData');
    navigate('/login');
  }, [navigate]);

  return null; // This component doesn't render anything
}