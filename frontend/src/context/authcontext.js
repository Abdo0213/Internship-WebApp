// context/AuthContext.js
import { createContext, useContext, useState } from 'react';
import { jwtDecode } from 'jwt-decode';

const AuthContext = createContext();

export function AuthProvider({ children }) {
    const [auth, setAuth] = useState(() => {
        const token = localStorage.getItem('accessToken');
        let isTokenValid = false;
        let decodedToken = null;
        if (token) {
            try {
                decodedToken = jwtDecode(token);
                isTokenValid = decodedToken.exp * 1000 > Date.now();
            } catch (err) {
                console.error("Invalid or corrupt token", err);
                isTokenValid = false;
            }
        }
        const userData = JSON.parse(localStorage.getItem('userData') || '{}');
        return token && isTokenValid ? {
            isAuthenticated: true,
            roles: decodedToken?.roles || [], // Get roles from the token
            ...userData
        } : {
            isAuthenticated: false,
            roles: [],
            userId: null,
            username: null
        };
    });

    const login = (authData) => {
        localStorage.setItem('accessToken', authData.accessToken);
        localStorage.setItem('userData', JSON.stringify({
            userId: authData.userId,
            username: authData.username,
            // Assuming roles are in the accessToken payload
        }));
        const decoded = jwtDecode(authData.accessToken);
        setAuth({
            isAuthenticated: true,
            roles: decoded?.roles || [], // Get roles from the token on login
            userId: authData.userId,
            username: authData.username
        });
    };

    const logout = () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('userData');
        setAuth({
            isAuthenticated: false,
            roles: [],
            userId: null,
            username: null
        });
    };

    return (
        <AuthContext.Provider value={{ ...auth, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
}