import React from 'react';
import Navbar from '../../components/Navbar/Navbar';
import './Landing.css';
import { Link } from 'react-router-dom';

const Landing = () => {
    const scrollToAbout = () => {
        const aboutSection = document.getElementById('about');
        if (aboutSection) {
        aboutSection.scrollIntoView({ behavior: 'smooth' });
        }
    };

    return (
        <div className="landing-page">
            <Navbar />
            
            <header className="hero">
                <div className="hero-overlay"></div>
                <img 
                    src="https://images.unsplash.com/photo-1497366811353-6870744d04b2?ixlib=rb-1.2.1&auto=format&fit=crop&w=1600&q=80" 
                    alt="Tech interns collaborating" 
                    className="hero-background"
                />
                <div className="hero-content">
                    <h1>Launch Your Tech Career</h1>
                    <p>Join our elite internship program designed to bridge the gap between academia and industry</p>
                    <div className="cta-buttons">
                        {localStorage.getItem("accessToken") ? null : <Link to="/login"><button className="primary-btn">Join US</button></Link>}
                        <button className="secondary-btn" onClick={scrollToAbout}>Learn More</button>
                    </div>
                </div>
            </header>

                <section id="about" className="about-section">
                    <div className="container">
                        <div className="section-header">
                            <h2>About Our Program</h2>
                            <p className="subtitle">Professional development in a competitive environment</p>
                        </div>
                        <div className="about-content">
                            <div className="about-text">
                                <h3>Transformative Learning Experience</h3>
                                <p>Our 12-week intensive program pairs you with industry mentors to work on real-world projects that matter. Unlike traditional internships, you'll be treated as a full team member from day one.</p>
                                
                                <h3>What Sets Us Apart</h3>
                                <ul className="about-features">
                                <li>Hands-on experience with cutting-edge technologies</li>
                                <li>Weekly one-on-one mentorship sessions</li>
                                <li>Portfolio-building projects you can showcase</li>
                                <li>75% conversion rate to full-time positions</li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </section>

            <section className="cta-section">
                <h2>Ready to Begin Your Journey?</h2>
                <p>Applications for our next cohort close June 30th</p>
                <div className="cta-buttons">
                    {localStorage.getItem("accessToken") ? null : <Link to="/login"><button className="primary-btn">Join US</button></Link>}
                </div>
            </section>
        </div>
    );
};

export default Landing;