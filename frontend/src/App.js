import './App.css';
import { AuthProvider } from './context/authcontext';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login/Login';
import Register from './pages/Register/Register';
import Landing from './pages/Landing/Landing';

function App() {
    return (
      <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* Public Routes */}
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/" element={<Landing />} />
          {/*<Route path="/unauthorized" element={<Unauthorized />} />*/}
          
          {/* Protected Routes 
          <Route 
            path="/" 
            element={
              <ProtectedRoute requiredRoles={['user']}>
                <Navigate to="/dashboard" replace />
              </ProtectedRoute>
            } 
          />
          
          <Route 
            path="/logout" 
            element={
              <ProtectedRoute requiredRoles={['user', 'editor']}>
                <Logout />
              </ProtectedRoute>
            } 
          />*/}
          
          {/* Fallback route */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
    );
}

export default App;
