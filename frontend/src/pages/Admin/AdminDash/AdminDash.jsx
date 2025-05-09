import React, { useState, useEffect } from 'react';
import DashboardBox from '../../../components/DashboardBox/DashboardBox';
import Navbar from '../../../components/Navbar/Navbar';
import '../Admin.css';
import './AdminDash.css';
import axios from 'axios';

const AdminDash = () => {
    const token = localStorage.getItem('accessToken');
    const [stats, setStats] = useState({
        companies: 0,
        hrs: 0,
        internships: 0
    });

    useEffect(() => {
        const fetchStats = async () => {
            try {
                const headers = {
                    Authorization: `Bearer ${token}`,
                };

                const internshipRes = await axios.get('http://localhost:8765/internship-service/internships/count', { headers });
                const hrsRes = await axios.get('http://localhost:8765/user-service/hr/count', { headers });
                const companiesRes = await axios.get('http://localhost:8765/user-service/companies/count', { headers });

                console.log('Internship Response:', internshipRes);
                console.log('HR Response:', hrsRes);
                console.log('Companies Response:', companiesRes);

                setStats({
                    companies: companiesRes.data, // Assuming the count is in the data property
                    hrs: hrsRes.data,
                    internships: internshipRes.data,
                });
            } catch (error) {
                console.error('Error fetching stats:', error);
                // Handle error appropriately, e.g., display an error message
            }
        };

        fetchStats();
    }, [token]);

    return (
        <div className="dashboard-page">
        <Navbar />
        <div className="dashboard-container">
            <h2>Admin Dashboard</h2>
            <div className="dashboard-boxes">
            <DashboardBox 
                title="Companies" 
                count={stats.companies} 
                addLink="/companies/add" 
                viewLink="/companies" 
            />
            <DashboardBox 
                title="HRs" 
                count={stats.hrs} 
                addLink="/hrs/add" 
                viewLink="/hrs" 
            />
            <DashboardBox 
                title="Internships" 
                count={stats.internships} 
                addLink="/internships/add" 
                viewLink="/internships" 
            />
            </div>
        </div>
        </div>
    );
};

export default AdminDash;