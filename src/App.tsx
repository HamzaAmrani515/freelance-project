import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import FreelanceListPage from './pages/freelance/FreelanceListPage';
import FreelanceAddPage from './pages/freelance/FreelanceAddPage';
import FreelanceEditPage from './pages/freelance/FreelanceEditPage';
import FreelanceDetailsPage from './pages/freelance/FreelanceDetailsPage';
import MissionPage from './pages/mission/MissionPage';
import ClientMissionsPage from './pages/mission/ClientMissionsPage';

import LoginPage from './pages/LoginPage';
import ClientPage from './pages/client/ClientPage'

import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { OpenAPI } from './api';

const App: React.FC = () => {
    OpenAPI.BASE = OpenAPI.BASE = process.env.REACT_APP_BACK_API_URL || 'http://default-api-url.com';

    return (
        <Router>
             <ToastContainer position="top-right" autoClose={3000} />
            <Routes>
                <Route path="/freelances" element={<FreelanceListPage />} />
                <Route path="/freelances/add" element={<FreelanceAddPage />} />
                <Route path="/freelances/edit/:id" element={<FreelanceEditPage />} />
                <Route path="/freelances/details/:id" element={<FreelanceDetailsPage />} />
                <Route path="/missions/:id" element={<MissionPage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/clients/:nom" element={<ClientPage />} />
                <Route path="/clients/:id/missions" element={<ClientMissionsPage />} />

            </Routes>
        </Router>
    );
};

export default App;