import './App.css';
import { AuthProvider } from './context/authcontext';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login/Login';
import Register from './pages/Register/Register';
import Landing from './pages/Landing/Landing';
import Profile from './pages/Profile/Profile';
import Notifications from './pages/Notification/Notifications';
import AdminDash from './pages/Admin/AdminDash/AdminDash';
import HRDash from './pages/HRDash/HRDash';
import Internship from './pages/Internship/Internship';
import Companies from './pages/Admin/Companies/Companies';
import HRsPageAdmin from './pages/Admin/HRsPageAdmin/HRsPageAdmin';
import InternshipsPage from './pages/Admin/InternshipsPageAdmin/InternshipsPageAdmin';
import Unauthorized from './context/Unauthorized';
import ProtectedRoute from './context/ProtectedRoute';

function App() {
    return (
      <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* Public Routes */}
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/unauthorized" element={<Unauthorized />} />
          <Route path="/" element={<Landing />} />
          <Route path="/internship" element={<Internship />} />
          <Route 
            path="/notifications" 
            element={
              <ProtectedRoute requiredRoles={['STUDENT']}>
                <Notifications />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/admin" 
            element={
              <ProtectedRoute requiredRoles={['ADMIN']}>
                <AdminDash />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/hr" 
            element={
              <ProtectedRoute requiredRoles={['HR']}>
                <HRDash />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/profile" 
            element={
              <ProtectedRoute requiredRoles={['STUDENT','ADMIN','HR']}>
                <Profile />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/companies" 
            element={
              <ProtectedRoute requiredRoles={['ADMIN']}>
                <Companies />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/hrs" 
            element={
              <ProtectedRoute requiredRoles={['ADMIN']}>
                <HRsPageAdmin />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/internships" 
            element={
              <ProtectedRoute requiredRoles={['ADMIN']}>
                <InternshipsPage />
              </ProtectedRoute>
            } 
          />


          {/* Fallback route */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
    );
}

export default App;
