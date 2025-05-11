import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/authcontext';
import { FaBars, FaTimes } from 'react-icons/fa';
import style from './Navbar.module.css';
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
        <nav className={style["mono-nav"]}>
            <div className={style["nav-container"]}>
                <div className={style["nav-logo"]}><Link to='/'>InternConnect</Link></div>

                {/* Desktop Navigation */}
                <div className={style["nav-links"]}>
                    <button onClick={() => handleScrollTo('about')} className={style["nav-link"]}>
                        About
                    </button>
                    {!role ? (
                        <Link to="/internship" className={style["nav-link"]}>
                            Internships
                        </Link>
                    ) : (role === "HR" ? (
                        <Link to={"/hr"} className={style["nav-link"]}>
                            Dashboard
                        </Link>
                    ) : (role === "ADMIN") ?
                        <>
                            <Link to="/admin" className={style["nav-link"]}>Dashboard</Link>
                            <Link to="/companies" className={style["nav-link"]}>Companies</Link>
                            <Link to="/hrs" className={style["nav-link"]}>HRs</Link>
                            <Link to="/internships" className={style["nav-link"]}>Internships</Link>
                        </>
                    : (
                        <>
                            <Link to="/internship" className={style["nav-link"]}>
                                Internships
                            </Link>
                            <Link to="/notifications" className={style["nav-link"]}>
                                Notifications
                            </Link>
                        </>
                    ))}
                    {isAuthenticated ? (
                        <div className={style["profile-dropdown"]}>
                            <button
                                className={style["profile-button"]}
                                onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                                aria-label="User menu"
                            >
                                <img
                                    src={`https://ui-avatars.com/api/?name=${username}&background=random`}
                                    alt="Profile"
                                    className={style["profile-photo"]}
                                />
                            </button>

                            {isDropdownOpen && (
                                <div className={style["dropdown-menu"]}>
                                    <Link
                                        to="/profile"
                                        className={style["dropdown-item"]}
                                        onClick={() => setIsDropdownOpen(false)}
                                    >
                                        Profile
                                    </Link>
                                    <button
                                        className={style["dropdown-item"]}
                                        onClick={handleLogout}
                                    >
                                        Logout
                                    </button>
                                </div>
                            )}
                        </div>
                    ) : (
                        <Link to="/login"><button className={style["nav-cta"]}>Join US</button></Link>
                    )}
                </div>

                {/* Mobile Navigation */}
                <div className={style["mobile-menu"]}>
                    <button
                        className={style["hamburger"]}
                        onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
                        aria-label={style["Menu"]}
                    >
                        {isMobileMenuOpen ? <FaTimes /> : <FaBars />}
                    </button>

                    {isMobileMenuOpen && (
                        <div className={style["mobile-dropdown"]}>
                            <button onClick={() => handleScrollTo('about')} className={style["mobile-nav-link"]}>
                                About
                            </button>
                            <Link to="/internship" className={style["mobile-nav-link"]} onClick={() => setIsMobileMenuOpen(false)}>
                                Internships
                            </Link>
                            {isAuthenticated && roles?.includes("STUDENT") && (
                                <Link to="/notifications" className={style["mobile-nav-link"]} onClick={() => setIsMobileMenuOpen(false)}>
                                    Notifications
                                </Link>
                            )}
                            {isAuthenticated ? (
                                <>
                                    <Link
                                        to="/profile"
                                        className={style["mobile-nav-link"]}
                                        onClick={() => setIsMobileMenuOpen(false)}
                                    >
                                        Profile
                                    </Link>
                                    <button
                                        className={style["mobile-nav-link"]}
                                        onClick={handleLogout}
                                    >
                                        Logout
                                    </button>
                                </>
                            ) : (
                                <Link
                                    to="/login"
                                    className={style["mobile-nav-cta"]}
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