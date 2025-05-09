import { useState, useEffect, useCallback } from 'react';
import Navbar from '../../../components/Navbar/Navbar';
import SearchableList from '../../../components/SearchableList/SearchableList';
import '../Admin.css';
import './Companies.css';
import axios from 'axios';
import EditInternshipModal from '../../../components/EditInternshipModal/EditInternshipModal';
import CreateInternshipModal from '../../../components/CreateInternshipModal/CreateInternshipModal';
import { FaPlus } from 'react-icons/fa';

const Companies = () => {
    const [companies, setCompanies] = useState([]);
    const [selectedCompany, setSelectedCompany] = useState(null);
    const token = localStorage.getItem("accessToken");
    const [internships, setInternships] = useState([]);
        const [selectedInternship, setSelectedInternship] = useState(null);
        const [page, setPage] = useState(1);
        const [searchQuery, setSearchQuery] = useState('');
        const [isLoading, setIsLoading] = useState(false);
        const [error, setError] = useState(null);
        const [hasMore, setHasMore] = useState(true);
        const [showCreateModal, setShowCreateModal] = useState(false);
        const [editedInternship, setEditedInternship] = useState(null);
        const [showEditModal, setShowEditModal] = useState(false);

    useEffect(() => {
        // Replace with actual API call
        const fetchCompanies = async () => {
            try {
                const headers = {
                    Authorization: `Bearer ${token}`,
                };
                const response = await axios.get('http://localhost:8765/user-service/companies', { headers });
                const data = response.data;
                console.log(data);
                // Make sure data is an array
                if (Array.isArray(data)) {
                    setCompanies(data);
                    if (data.length > 0) {
                        setSelectedCompany(data[0]);
                    }
                } else {
                    console.error('Expected an array but got:', data);
                }
            } catch (error) {
                console.error('Error fetching HRs:', error);
            }
        };

        fetchCompanies();
    }, [token]);

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
                    <div className="page-header">
                        <h2>Companies</h2>
                        <button
                            className="btn btn-add"
                            onClick={() => setShowCreateModal(true)}
                        >
                            Add New Company <FaPlus/>
                        </button>
                    </div>
                    
                    <div className="two-column-layout">
                        <div className="list-column">
                            <SearchableList 
                                items={companies} 
                                onItemSelect={setSelectedCompany} 
                                selectedItem={selectedCompany} 
                            />
                        </div>
                        
                        <div className="detail-column">
                            {selectedCompany ? (
                            <div className="detail-card">
                                <h3>{selectedCompany.name}</h3>
                                <div className="detail-item">
                                    <span className="detail-label">Industry:</span>
                                    <span>{selectedCompany.industry}</span>
                                </div>
                                <div className="detail-item">
                                    <span className="detail-label">Location:</span>
                                    <span>{selectedCompany.location}</span>
                                </div>
                                <div className="card-actions">
                                    <button className="btn btn-edit">Edit</button>
                                    <button className="btn btn-delete">Delete</button>
                                </div>
                            </div>
                            ) : (
                            <p>Select a company to view details</p>
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
                    modalType={"company"}
                />
            )}
            {showCreateModal && (
                <CreateInternshipModal
                    show={showCreateModal}
                    onClose={() => setShowCreateModal(false)}
                    onCreate={handleCreateInternship}
                    modalType={"company"}
                />
            )}
        </>
    );
};

export default Companies;