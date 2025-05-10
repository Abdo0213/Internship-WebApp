import React, { useState, useEffect, useCallback } from 'react';
import axios from 'axios';
import './HRDash.css';
import Navbar from "../../components/Navbar/Navbar";
import EditInternshipModal from '../../components/EditInternshipModal/EditInternshipModal';
import ApplicationsList from '../../components/ApplicationListbyIntern/ApplicationListbyIntern';
import InternshipDetails from '../../components/InternshipDetails/InternshipDetails';
import CreateInternshipModal from '../../components/CreateInternshipModal/CreateInternshipModal';
import { useNavigate } from 'react-router-dom'; // Import useNavigate
import { FaPlus } from 'react-icons/fa';


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
    const [showCreateModal, setShowCreateModal] = useState(false);
    const [companyName, setCompanyName] = useState(null);

    const token = localStorage.getItem('accessToken');
    const hrId = JSON.parse(localStorage.getItem("userData"))?.userId;
    const navigate = useNavigate(); // Initialize useNavigate

    useEffect(() => {
        // Check if token exists on component mount
        if (!token) {
            // Redirect to login page if no token
            navigate('/login'); // Replace '/login' with your actual login route
            return; // Stop further execution of the component
        }
    }, [token, navigate]);

    // Fetch internships with pagination and search
    const fetchInternships = useCallback(async (pageNum, search = '') => {
        if (!token) {
            console.error('No access token available');
            return;
        }
        setIsLoading(true);
        setError(null);
        let isMounted = true; // Add this flag

        try {
            const params = {
                page: pageNum - 1,
                size: 10
            };

            if (search.trim() !== '') {
                params.search = search.trim();
            }

            const response = await axios.get(
                `http://localhost:8765/internship-service/internships/hr/${hrId}`,
                {
                    params,
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                }
            );

            const newInternships = response.data?.content || [];
            if (isMounted) { // Check if component is mounted
                if (pageNum === 1) {
                    setInternships(newInternships);
                    if (newInternships.length > 0) {
                        setSelectedInternship(newInternships[0]);
                        setCompanyName(newInternships[0].companyName);
                    } else {
                        setSelectedInternship(null);
                        setCompanyName(null);
                    }
                } else {
                    setInternships(prev => [...prev, ...newInternships]);
                }
                setHasMore(!response.data.last);
                setPage(pageNum);
            }
        } catch (err) {
            if (isMounted) { // Check if component is mounted
                setError(err.response?.data?.message || 'Failed to fetch internships');
                console.error('Search error:', err);
            }
        } finally {
            if (isMounted) { // Check if component is mounted
                setIsLoading(false);
            }
        }
        return () => {
            isMounted = false; // Set flag to false on unmount
        };
    }, [token, hrId]); // Added navigate to dependencies

    // Fetch applications for selected internship
    const fetchApplications = useCallback(async () => {
        if (!token) {
            console.error('No access token available');
            return;
        }
        if (!selectedInternship) return;

        setIsLoading(true);
        let isMounted = true;
        try {
            const response = await axios.get(
                `http://localhost:8765/internship-service/applications/internship/${selectedInternship.id}`,
                {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                }
            );
            if (isMounted) {
                setApplications(response.data);
            }
        } catch (err) {
            if (isMounted) {
                setError(err.response?.data?.message || 'Failed to fetch applications');
            }
        } finally {
            if (isMounted) {
                setIsLoading(false);
            }
        }
        return () => {
            isMounted = false;
        };
    }, [selectedInternship, token]);

    // Debounce search input
    useEffect(() => {
        const handler = setTimeout(() => {
            setDebouncedSearchTerm(searchTerm);
        }, 500);
        return () => clearTimeout(handler);
    }, [searchTerm]);

    // Trigger fetch when debounced search term changes
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
        if (isLoading || !hasMore || !token) return; // Check for token

        const { scrollTop, scrollHeight, clientHeight } = document.documentElement;
        if (scrollTop + clientHeight >= scrollHeight - 100) {
            setPage(prev => prev + 1);
        }
    }, [isLoading, hasMore, token]);

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
        if (!token) {
            console.error('No access token available');
            return;
        }
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
        if (!token) {
            console.error('No access token available');
            return;
        }
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
                internship.id === response.data.id ? response.data : internship
            ));

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

    const handleCreateInternship = async (internshipData) => {
        if (!token) {
            console.error('No access token available');
            return;
        }
        try {
            const response = await axios.post(
                `http://localhost:8765/internship-service/internships`,
                { ...internshipData, hrId: hrId, companyName: companyName }, // Include companyName here
                {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                }
            );

            // Add the new internship to the list and select it
            setInternships(prev => [response.data, ...prev]);
            setSelectedInternship(response.data);
            setShowCreateModal(false);

            // Optionally show success message
        } catch (error) {
            console.error('Error creating internship:', error);
            setError('Failed to create internship');
        }
    };
    
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
                        {isLoading && <span className="search-loading">Searching...</span>}
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
                                        {new Date(internship.createdAt).toLocaleDateString()}
                                    </p>
                                </div>
                            ))
                        )}
                        {isLoading && page > 1 && <div className="loading">Loading more...</div>}
                    </div>
                </div>

                {/* Main Content - Internship Details and Applications */}
                <div className="main-content">
                    <div className="main-content-header">
                        <button
                            className="new-internship-button"
                            onClick={() => setShowCreateModal(true)}
                        >
                            Create New Internship <FaPlus/>
                        </button>
                    </div>
                    {selectedInternship ? (
                        <>
                            <InternshipDetails
                                internship={selectedInternship}
                                onEditClick={() => setShowEditModal(true)}
                            />

                            <ApplicationsList
                                applications={applications}
                                onStatusChange={handleStatusChange}
                            />
                        </>
                    ) : (
                        <div className="no-internship-selected">
                            <p>Select an internship from the list or create a new one</p>
                        </div>
                    )}
                </div>
            </div>

            {/* Edit Internship Modal */}
            {showEditModal && editedInternship && (
                <EditInternshipModal
                    item={editedInternship}
                    onSave={handleSaveInternship}
                    onCancel={() => setShowEditModal(false)}
                    onInputChange={handleInputChange}
                    modalType={"internship"}
                />
            )}
            {showCreateModal && (
                <CreateInternshipModal
                    show={showCreateModal}
                    onClose={() => setShowCreateModal(false)}
                    onCreate={handleCreateInternship}
                    modalType={"internship"}
                />
            )}
        </>
    );
};

export default HRDash;