import React from 'react'
import './Navbar.css'
import { Link } from 'react-router-dom'

const Navbar = () => {
    return (
        <nav className="mono-nav">
            <div className="nav-container">
                <div className="nav-logo"><Link to='/'>InternConnect</Link></div>
                <div className="nav-links">
                    <a href="#about" className="nav-link">About</a>
                    <a href="#benefits" className="nav-link">Internships</a>
                    <a href="#testimonials" className="nav-link">Notifications</a>
                    <Link to ="/login"><button className="nav-cta">Join US</button></Link>
                </div>
                {/* Mobile menu button would go here */}
            </div>
        </nav>
    )
}

export default Navbar