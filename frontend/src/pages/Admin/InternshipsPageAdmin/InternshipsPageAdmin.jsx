import React, { useState, useEffect, useCallback } from 'react';
import Navbar from '../../../components/Navbar/Navbar';
import SearchableList from '../../../components/SearchableList/SearchableList';
import '../Admin.css';
import './InternshipsPageAdmin.css';
import axios from 'axios';
import InternshipDetails from '../../../components/InternshipDetails/InternshipDetails';
import { FaPlus } from 'react-icons/fa';
import EditInternshipModal from '../../../components/EditInternshipModal/EditInternshipModal';
import CreateInternshipModal from '../../../components/CreateInternshipModal/CreateInternshipModal';

const InternshipsPage = () => {
    const [internships, setInternships] = useState([]);
    const [selectedInternship, setSelectedInternship] = useState(null);
    const [page, setPage] = useState(1);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);
    const [hasMore, setHasMore] = useState(true);
    const [showCreateModal, setShowCreateModal] = useState(false);
    const [editedInternship, setEditedInternship] = useState(null);
    const [showEditModal, setShowEditModal] = useState(false);
    const token = localStorage.getItem('accessToken');
    
    

    useEffect(() => {
        let isMounted = true; // For cleanup
        
        const fetchInternships = async (pageNum, search = '') => {
            setIsLoading(true);
            setError(null);
            
            try {
                const config = {
                    params: {
                        page: pageNum - 1,
                        size: 10,
                        search: search
                    },
                    headers: token ? { Authorization: `Bearer ${token}` } : {}
                };
                const response = await axios.get('http://localhost:8765/internship-service/internships', config);
                if (!isMounted) return;
                
                const newInternships = response.data?.content || [];
                
                setInternships(prev => 
                    pageNum === 1 ? newInternships : [...prev, ...newInternships]
                );
                
                setHasMore(newInternships.length > 0);
                
                if (newInternships.length > 0 && !selectedInternship) {
                    setSelectedInternship(newInternships[0]);
                }
            } catch (err) {
                if (isMounted) {
                    setError(err.response?.data?.message || 'Failed to fetch internships');
                }
            } finally {
                if (isMounted) {
                    setIsLoading(false);
                }
            }
        };

        // Initial fetch
        fetchInternships(1, '');
        
        return () => {
            isMounted = false; // Cleanup function
        };
    }, [selectedInternship]); // Add other dependencies if needed

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
                { ...internshipData }, // Include companyName here
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

    return (
        <>
            <Navbar />
            <div className="page-container">
                <div className="content-container">
                    <div className="two-column-layout">
                        <div className="list-column">
                            <SearchableList 
                            items={internships} 
                            onItemSelect={setSelectedInternship} 
                            selectedItem={selectedInternship} 
                            />
                        </div>
                        
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
                                    <div className="card-actions">
                                        {/*<button className="btn btn-edit">Edit</button>*/}
                                        <button className="btn btn-delete">Delete</button>
                                    </div>
                                </>
                            ) : (
                                <div className="no-internship-selected">
                                    <p>Select an internship from the list or create a new one</p>
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </div>
            {/* Edit Internship Modal */}
            {showEditModal && editedInternship && (
                <EditInternshipModal
                    internship={editedInternship}
                    onSave={handleSaveInternship}
                    onCancel={() => setShowEditModal(false)}
                    onInputChange={handleInputChange}
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

export default InternshipsPage;