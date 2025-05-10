import { useState, useEffect } from 'react';
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
    const [error, setError] = useState(null);
    const [showCreateModal, setShowCreateModal] = useState(false);
    const [editedCompany, setEditedCompany] = useState(null);
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

    const handleSaveCompany = async () => {
        if (!token) {
            console.error('No access token available');
            return;
        }
        try {
            const response = await axios.put(
                `http://localhost:8765/user-service/companies/${editedCompany.id}`,
                editedCompany,
                {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                }
            );

            // Update the selected internship
            setSelectedCompany(response.data);
            // Update the internships list
            setCompanies(companies.map(company =>
                company.id === response.data.id ? response.data : company
            ));

            setShowEditModal(false);
            // Optionally show success message
        } catch (error) {
            console.error('Error updating company:', error);
            setError('Failed to update company');
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setEditedCompany(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleCreateCompany = async (companiesData) => {
        if (!token) {
            console.error('No access token available');
            return;
        }
        try {
            const response = await axios.post(
                `http://localhost:8765/user-service/companies`,
                { ...companiesData }, // Include companyName here
                {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                }
            );
            setCompanies(prev => [response.data, ...prev]);
            setSelectedCompany(response.data);
            setShowCreateModal(false);

            // Optionally show success message
        } catch (error) {
            console.error('Error creating company:', error);
            setError('Failed to create company');
        }
    };
    useEffect(() => {
        if (selectedCompany) {
            setEditedCompany({ ...selectedCompany });
        }
    }, [selectedCompany]);

    const handleDeleteCompany = async(id) => {
        if (!token) {
            console.error('No access token available');
            return;
        }
        try {
            setCompanies(prev => {
                const updatedCompanies = prev.filter(company => company.id !== id);
                setSelectedCompany(updatedCompanies.length > 0 ? updatedCompanies[0] : null);
                return updatedCompanies;
            });
            await axios.delete(
                `http://localhost:8765/user-service/companies/${id}`,
                {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                }
            );
            setShowCreateModal(false);

        } catch (error) {
            console.error('Error deleting company:', error);
            setError('Failed to deleting company');
        }
    }
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
                                    <button className="btn btn-edit" onClick={() => setShowEditModal(true)}>Edit</button>
                                    <button className="btn btn-delete" onClick={() => handleDeleteCompany(selectedCompany.id)}>Delete</button>
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
            {showEditModal && editedCompany && (
                <EditInternshipModal
                    item={editedCompany}
                    onSave={handleSaveCompany}
                    onCancel={() => setShowEditModal(false)}
                    onInputChange={handleInputChange}
                    modalType={"company"}
                />
            )}
            {showCreateModal && (
                <CreateInternshipModal
                    show={showCreateModal}
                    onClose={() => setShowCreateModal(false)}
                    onCreate={handleCreateCompany}
                    modalType={"company"}
                />
            )}
        </>
    );
};

export default Companies;