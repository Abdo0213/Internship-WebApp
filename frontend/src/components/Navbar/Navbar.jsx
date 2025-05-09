import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/authcontext';
import { FaBars, FaTimes } from 'react-icons/fa';
import './Navbar.css';
import { jwtDecode } from 'jwt-decode';
import { flushSync } from 'react-dom';

const Navbar = () => {
    const [isDropdownOpen, setIsDropdownOpen] = useState(false);
    const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
    const { isAuthenticated, logout, username, roles } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();
    const token = localStorage.getItem("accessToken");
    const decoded = token ? jwtDecode(token) : null;
    const role = decoded?.role; // Safely access role

    const handleScrollTo = (sectionId) => {
        setIsMobileMenuOpen(false);
        if (location.pathname !== '/') {
            navigate('/');
            setTimeout(() => {
                scrollToSection(sectionId);
            }, 100);
        } else {
            scrollToSection(sectionId);
        }
    };

    const scrollToSection = (sectionId) => {
        const element = document.getElementById(sectionId);
        if (element) {
            element.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        }
    };

    const handleLogout = () => {
        flushSync(() => {
            logout();
        });
        setIsDropdownOpen(false);
        setIsMobileMenuOpen(false);
        navigate('/login');
    };

    return (
        <nav className="mono-nav">
            <div className="nav-container">
                <div className="nav-logo"><Link to='/'>InternConnect</Link></div>

                {/* Desktop Navigation */}
                <div className="nav-links">
                    <button onClick={() => handleScrollTo('about')} className="nav-link">
                        About
                    </button>
                    {!role ? (
                        <Link to="/internship" className="nav-link">
                            Internships
                        </Link>
                    ) : (role === "HR" ? (
                        <Link to={"/hr"} className="nav-link">
                            Dashboard
                        </Link>
                    ) : (role === "ADMIN") ?
                        <>
                            <Link to="/admin" className="nav-link">Dashboard</Link>
                            <Link to="/companies" className="nav-link">Companies</Link>
                            <Link to="/hrs" className="nav-link">HRs</Link>
                            <Link to="/internships" className="nav-link">Internships</Link>
                        </>
                    : (
                        <>
                            <Link to="/internship" className="nav-link">
                                Internships
                            </Link>
                            <Link to="/notifications" className="nav-link">
                                Notifications
                            </Link>
                        </>
                    ))}
                    {isAuthenticated ? (
                        <div className="profile-dropdown">
                            <button
                                className="profile-button"
                                onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                                aria-label="User menu"
                            >
                                <img
                                    src={`https://ui-avatars.com/api/?name=${username}&background=random`}
                                    alt="Profile"
                                    className="profile-photo"
                                />
                            </button>

                            {isDropdownOpen && (
                                <div className="dropdown-menu">
                                    <Link
                                        to="/profile"
                                        className="dropdown-item"
                                        onClick={() => setIsDropdownOpen(false)}
                                    >
                                        Profile
                                    </Link>
                                    <button
                                        className="dropdown-item"
                                        onClick={handleLogout}
                                    >
                                        Logout
                                    </button>
                                </div>
                            )}
                        </div>
                    ) : (
                        <Link to="/login"><button className="nav-cta">Join US</button></Link>
                    )}
                </div>

                {/* Mobile Navigation */}
                <div className="mobile-menu">
                    <button
                        className="hamburger"
                        onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
                        aria-label="Menu"
                    >
                        {isMobileMenuOpen ? <FaTimes /> : <FaBars />}
                    </button>

                    {isMobileMenuOpen && (
                        <div className="mobile-dropdown">
                            <button onClick={() => handleScrollTo('about')} className="mobile-nav-link">
                                About
                            </button>
                            <Link to="/internship" className="mobile-nav-link" onClick={() => setIsMobileMenuOpen(false)}>
                                Internships
                            </Link>
                            {isAuthenticated && roles?.includes("STUDENT") && (
                                <Link to="/notifications" className="mobile-nav-link" onClick={() => setIsMobileMenuOpen(false)}>
                                    Notifications
                                </Link>
                            )}
                            {isAuthenticated ? (
                                <>
                                    <Link
                                        to="/profile"
                                        className="mobile-nav-link"
                                        onClick={() => setIsMobileMenuOpen(false)}
                                    >
                                        Profile
                                    </Link>
                                    <button
                                        className="mobile-nav-link"
                                        onClick={handleLogout}
                                    >
                                        Logout
                                    </button>
                                </>
                            ) : (
                                <Link
                                    to="/login"
                                    className="mobile-nav-cta"
                                    onClick={() => setIsMobileMenuOpen(false)}
                                >
                                    Join US
                                </Link>
                            )}
                        </div>
                    )}
                </div>
            </div>
        </nav>
    );
};

export default Navbar;