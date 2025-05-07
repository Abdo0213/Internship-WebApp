import React, { useState, useEffect, useCallback } from 'react';
import axios from 'axios';
import './HRDash.css';
import Navbar from "../../components/Navbar/Navbar";
import { FiChevronDown, FiChevronUp } from 'react-icons/fi'; // Import arrow icons


const HRDash = () => {
    // State management
    const [internships, setInternships] = useState([]);
    const [selectedInternship, setSelectedInternship] = useState(null);
    const [applications, setApplications] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [debouncedSearchTerm, setDebouncedSearchTerm] = useState('');
    const [page, setPage] = useState(1);
    const [hasMore, setHasMore] = useState(true);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);
    const [showEditModal, setShowEditModal] = useState(false);
    const [editedInternship, setEditedInternship] = useState(null);
    const [isExpanded, setIsExpanded] = useState(false);
    
    const token = localStorage.getItem('accessToken');
    const hrId = JSON.parse(localStorage.getItem("userData"))?.userId;

    // Fetch internships with pagination and search
    const fetchInternships = useCallback(async (pageNum, search = '') => {
        setIsLoading(true);
        setError(null);
    
        try {
            const config = {
                params: {
                    page: pageNum - 1, // Spring pages are 0-indexed
                    size: 10,
                    search: search
                },
                headers: {
                    Authorization: `Bearer ${token}`
                }
            };
    
            const response = await axios.get(
                `http://localhost:8765/internship-service/internships/hr/${hrId}`,
                config
            );
            
            const newInternships = response.data?.content || [];
            if (pageNum === 1) {
                setInternships(newInternships);
            } else {
                setInternships(prev => [...prev, ...newInternships]);
            }
    
            setHasMore(!response.data.last); // Check if it's the last page
            setPage(pageNum);
    
            // Auto-select first internship if none selected
            if (newInternships.length > 0 && !selectedInternship) {
                setSelectedInternship(newInternships[0]);
            }
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to fetch internships');
        } finally {
            setIsLoading(false);
        }
    }, [token, hrId, selectedInternship]);

    // Fetch applications for selected internship
    const fetchApplications = useCallback(async () => {
        if (!selectedInternship) return;
        
        setIsLoading(true);
        try {
            const response = await axios.get(
                `http://localhost:8765/internship-service/applications/internship/${selectedInternship.id}`,
                {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                }
            );
            setApplications(response.data);
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to fetch applications');
        } finally {
            setIsLoading(false);
        }
    }, [selectedInternship, token]);

    // Debounce search input
    useEffect(() => {
        const handler = setTimeout(() => {
            setDebouncedSearchTerm(searchTerm);
        }, 500);
        return () => clearTimeout(handler);
    }, [searchTerm]);

    // Fetch internships when debounced search term changes
    useEffect(() => {
        setPage(1);
        fetchInternships(1, debouncedSearchTerm);
    }, [debouncedSearchTerm, fetchInternships]);

    // Fetch applications when selected internship changes
    useEffect(() => {
        fetchApplications();
    }, [selectedInternship, fetchApplications]);

    // Initialize editedInternship when selectedInternship changes
    useEffect(() => {
        if (selectedInternship) {
            setEditedInternship({ ...selectedInternship });
        }
    }, [selectedInternship]);

    // Infinite scroll handler
    const handleScroll = useCallback(() => {
        if (isLoading || !hasMore) return;
        
        const { scrollTop, scrollHeight, clientHeight } = document.documentElement;
        if (scrollTop + clientHeight >= scrollHeight - 100) {
            setPage(prev => prev + 1);
        }
    }, [isLoading, hasMore]);

    useEffect(() => {
        window.addEventListener('scroll', handleScroll);
        return () => window.removeEventListener('scroll', handleScroll);
    }, [handleScroll]);

    // Fetch more internships when page increases
    useEffect(() => {
        if (page > 1) {
            fetchInternships(page, debouncedSearchTerm);
        }
    }, [page, debouncedSearchTerm, fetchInternships]);

    // Handle application status change
    const handleStatusChange = async (applicationId, newStatus) => {
        try {
            await axios.put(
                `http://localhost:8765/internship-service/applications/${applicationId}`,
                { status: newStatus },
                {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                }
            );
            setApplications(applications.map(app =>
                app.id === applicationId ? { ...app, status: newStatus } : app
            ));
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to update application status');
        }
    };

    // Handle internship edit save
    const handleSaveInternship = async () => {
        try {
            const response = await axios.put(
                `http://localhost:8765/internship-service/internships/${editedInternship.id}`,
                editedInternship,
                {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                }
            );
            
            // Update the selected internship
            setSelectedInternship(response.data);
            
            // Update the internships list
            setInternships(internships.map(internship => 
                internship.id === response.data.id ? response.data : internship)
            );
            
            setShowEditModal(false);
            // Optionally show success message
        } catch (error) {
            console.error('Error updating internship:', error);
            setError('Failed to update internship');
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setEditedInternship(prev => ({
            ...prev,
            [name]: value
        }));
    };

    // Render your component UI here
    return (
        <>
            <Navbar/>
            <div className="internship-management-container">
                {/* Left Sidebar - Internships List */}
                <div className="internship-sidebar">
                    <div className="search-container">
                        <input
                            type="text"
                            placeholder="Search internships..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="search-input"
                        />
                    </div>

                    <div className="internship-list">
                        {isLoading && page === 1 ? (
                            <div className="loading">Loading...</div>
                        ) : error ? (
                            <div className="error">{error}</div>
                        ) : (
                            internships.map((internship) => (
                                <div
                                    key={internship.id}
                                    className={`internship-item ${selectedInternship?.id === internship.id ? 'selected' : ''}`}
                                    onClick={() => setSelectedInternship(internship)}
                                >
                                    <h3>{internship.title}</h3>
                                    <p className="company">{internship.companyName}</p>
                                    <p className="dates">
                                        {new Date(internship.createdAt).toLocaleDateString()} -{' '}
                                        {new Date(internship.expiresAt).toLocaleDateString()}
                                    </p>
                                </div>
                            ))
                        )}
                        {isLoading && page > 1 && <div className="loading">Loading more...</div>}
                    </div>
                </div>

                {/* Main Content - Internship Details and Applications */}
                <div className="main-content">
                    {selectedInternship && (
                        <>
                            {/* Internship Details */}
                            <div className="details-section">
                                <div className="content-header">
                                    <h2>{selectedInternship.title}</h2>
                                    <button 
                                        className="edit-btn" 
                                        onClick={() => setShowEditModal(true)}
                                    >
                                        Edit Internship
                                    </button>
                                </div>

                                <div className="internship-details">
                                    <div className='details-grid'>
                                        <div className="detail-item">
                                            <span className="detail-label">Company:</span>
                                            <span className="detail-value">{selectedInternship.companyName}</span>
                                        </div>
                                        <div className="detail-item">
                                            <span className="detail-label">Type:</span>
                                            <span className="detail-value">{selectedInternship.type}</span>
                                        </div>
                                        <div className="detail-item">
                                            <span className="detail-label">Stipend:</span>
                                            <span className="detail-value">{selectedInternship.stipend}</span>
                                        </div>
                                        <div className="detail-item">
                                            <span className="detail-label">Location:</span>
                                            <span className="detail-value">{selectedInternship.location}</span>
                                        </div>
                                        <div className="detail-item">
                                            <span className="detail-label">Duration:</span>
                                            <span className="detail-value">{selectedInternship.duration}</span>
                                        </div>
                                        <div className="detail-item">
                                            <span className="detail-label">Posted:</span>
                                            <span className="detail-value">
                                                {new Date(selectedInternship.createdAt).toLocaleDateString()}
                                            </span>
                                        </div>
                                        <div className="detail-item">
                                            <span className="detail-label">Apply by:</span>
                                            <span className="detail-value">
                                                {new Date(selectedInternship.expiresAt).toLocaleDateString()}
                                            </span>
                                        </div>
                                    </div>
                                </div>

                                <div className="description-section">
                                    <h3>Description</h3>
                                    <p>{selectedInternship.description}</p>
                                </div>
                            </div>

                            {/* Applications List */}
                            <div className="applications-section">
                                <div 
                                    className="section-header" 
                                    onClick={() => setIsExpanded(!isExpanded)}
                                    style={{ cursor: 'pointer' }}
                                >
                                    <h2>Applications</h2>
                                    <span className="toggle-arrow">
                                    {isExpanded ? <FiChevronUp size={24} /> : <FiChevronDown size={24} />}
                                    </span>
                                </div>
                                
                                { !isExpanded ? null : applications.length === 0 ? (
                                    <p className="no-applications">No applications found</p>
                                ) : (
                                    <div className="applications-list">
                                        {applications.map((application) => (
                                            <div key={application.id} className="application-card">
                                                <div className="student-info">
                                                    <h3>{application.studentName}</h3>
                                                    <div className="student-details">
                                                        <p><strong>College:</strong> {application.studentCollege}</p>
                                                        <p><strong>Faculty:</strong> {application.studentFaculty}</p>
                                                        <p><strong>Grade:</strong> {application.studentGrade}</p>
                                                        <p><strong>CV:</strong> 
                                                            <a href={`/path/to/cvs/${application.studentCV}`} target="_blank" rel="noopener noreferrer">
                                                                {application.studentCV}
                                                            </a>
                                                        </p>
                                                    </div>
                                                </div>
                                                
                                                <div className="application-meta">
                                                    <div className={`status-badge ${application.status.toLowerCase()}`}>
                                                        {application.status}
                                                    </div>
                                                    <p className="applied-date">
                                                        Applied: {new Date(application.appliedAt).toLocaleDateString()}
                                                    </p>
                                                </div>
                                                
                                                {application.status === "PENDING" && (
                                                    <div className="application-actions">
                                                        <button 
                                                            className="accept-btn"
                                                            onClick={() => handleStatusChange(application.id, 'ACCEPTED')}
                                                            disabled={application.status === 'ACCEPTED'}
                                                        >
                                                            Accept
                                                        </button>
                                                        <button 
                                                            className="reject-btn"
                                                            onClick={() => handleStatusChange(application.id, 'REJECTED')}
                                                            disabled={application.status === 'REJECTED'}
                                                        >
                                                            Reject
                                                        </button>
                                                    </div>
                                                )}
                                            </div>
                                        ))}
                                    </div>
                                )}
                            </div>
                        </>
                    )}
                </div>
            </div>

            {/* Edit Internship Modal */}
            {showEditModal && editedInternship && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>Edit Internship</h2>
                        
                        <form onSubmit={(e) => {
                            e.preventDefault();
                            handleSaveInternship();
                        }}>
                            <div className="form-group">
                                <label>Title</label>
                                <input
                                    type="text"
                                    name="title"
                                    value={editedInternship.title || ''}
                                    onChange={handleInputChange}
                                    required
                                />
                            </div>

                            <div className="form-row">
                                <div className="form-group">
                                    <label>Stipend</label>
                                    <input
                                        type="text"
                                        name="stipend"
                                        value={editedInternship.stipend || ''}
                                        onChange={handleInputChange}
                                        required
                                    />
                                </div>
                                <div className="form-group">
                                    <label>Type</label>
                                    <select
                                        name="type"
                                        value={editedInternship.type || ''}
                                        onChange={handleInputChange}
                                        required
                                    >
                                        <option value="REMOTE">Remote</option>
                                        <option value="HYBRID">Hybrid</option>
                                        <option value="ONSITE">On-site</option>
                                    </select>
                                </div>
                            </div>

                            <div className="form-row">
                                <div className="form-group">
                                    <label>Location</label>
                                    <input
                                        type="text"
                                        name="location"
                                        value={editedInternship.location || ''}
                                        onChange={handleInputChange}
                                        required
                                    />
                                </div>
                                <div className="form-group">
                                    <label>Duration</label>
                                    <input
                                        type="text"
                                        name="duration"
                                        value={editedInternship.duration || ''}
                                        onChange={handleInputChange}
                                        required
                                    />
                                </div>
                            </div>

                            <div className="form-group">
                                <label>Description</label>
                                <textarea
                                    name="description"
                                    value={editedInternship.description || ''}
                                    onChange={handleInputChange}
                                    rows={5}
                                    required
                                />
                            </div>

                            <div className="modal-actions">
                                <button 
                                    type="button" 
                                    className="cancel-button"
                                    onClick={() => setShowEditModal(false)}
                                >
                                    Cancel
                                </button>
                                <button 
                                    type="submit" 
                                    className="submit-button"
                                >
                                    Save Changes
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </>
    );
};

export default HRDash;