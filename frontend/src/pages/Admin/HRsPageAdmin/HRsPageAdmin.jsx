import { useState, useEffect } from 'react';
import Navbar from '../../../components/Navbar/Navbar';
import SearchableList from '../../../components/SearchableList/SearchableList';
import '../Admin.css';
import './HRsPageAdmin.css';
import axios from 'axios';
import CreateInternshipModal from '../../../components/CreateInternshipModal/CreateInternshipModal';
import { FaPlus } from 'react-icons/fa';

const HRsPageAdmin = () => {
    const token = localStorage.getItem('accessToken');
    const [hrs, setHrs] = useState([]);
    const [selectedHr, setSelectedHr] = useState(null);
    const [error, setError] = useState(null);
    const [showCreateModal, setShowCreateModal] = useState(false);

    useEffect(() => {
        const fetchHrs = async () => {
            try {
                const headers = {
                    Authorization: `Bearer ${token}`,
                };
                const response = await axios.get('http://localhost:8765/user-service/hr', { headers });
                const data = response.data;
                
                // Make sure data is an array
                if (Array.isArray(data)) {
                    setHrs(data);
                    if (data.length > 0) {
                        setSelectedHr(data[0]);
                    }
                } else {
                    console.error('Expected an array but got:', data);
                }
            } catch (error) {
                console.error('Error fetching HRs:', error);
            }
        };
        
        fetchHrs();
    }, [token]);

    const handleAddHR = async (hrData) => {
        if (!token) {
            console.error('No access token available');
            return;
        }
        try {
            const response = await axios.post(
                `http://localhost:8765/user-service/hr`,
                { ...hrData }, // Include companyName here
                {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                }
            );

            // Add the new internship to the list and select it
            setHrs(prev => [response.data, ...prev]);
            setSelectedHr(response.data);
            setShowCreateModal(false);

            // Optionally show success message
        } catch (error) {
            console.error('Error creating internship:', error);
            setError('Failed to create internship');
        }
    };

    return (
        <>
            <Navbar />
            <div className="page-container">
                <div className="content-container">
                    <div className="page-header">
                        <h2>HR Managers</h2>
                        <button
                            className="btn btn-add"
                            onClick={() => setShowCreateModal(true)}
                        >
                            Add New HR <FaPlus/>
                        </button>
                    </div>
                    
                    <div className="two-column-layout">
                        {<div className="list-column">
                            {/* Uncomment and ensure SearchableList can handle object items */}
                            <SearchableList 
                                items={hrs} 
                                onItemSelect={setSelectedHr} 
                                selectedItem={selectedHr} 
                                displayProperty="user.fname" // Tell the list which property to display
                            />
                        </div>}
                        
                        <div className="detail-column">
                            {selectedHr ? (
                                <div className="detail-card">
                                    <h3>{selectedHr.user.fname}</h3>
                                    <div className="detail-item">
                                        <span className="detail-label">Email:</span>
                                        <span>{selectedHr.user.email || 'N/A'}</span>
                                    </div>
                                    <div className="detail-item">
                                        <span className="detail-label">Company:</span>
                                        <span>{selectedHr.company.name}</span>
                                    </div>
                                    <div className="card-actions">
                                        {/*<button className="btn btn-edit">Edit</button>*/}
                                        <button className="btn btn-delete">Delete</button>
                                    </div>
                                </div>
                            ) : (
                                <p>Select an HR to view details</p>
                            )}
                        </div>
                    </div>
                </div>
            </div>
            {showCreateModal && (
                <CreateInternshipModal
                    show={showCreateModal}
                    onClose={() => setShowCreateModal(false)}
                    onCreate={handleAddHR}
                    modalType={"hr"}
                />
            )}
        </>
    );
};

export default HRsPageAdmin;