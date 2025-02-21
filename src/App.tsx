import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import FreelanceListPage from './pages/FreelanceListPage';
import FreelanceAddPage from './pages/FreelanceAddPage';
import FreelanceEditPage from './pages/FreelanceEditPage';
import FreelanceDetailsPage from './pages/FreelanceDetailsPage';
import MissionPage from './pages/MissionPage';

import { OpenAPI } from './api';

const App: React.FC = () => {
    OpenAPI.BASE = OpenAPI.BASE = process.env.REACT_APP_BACK_API_URL || 'http://default-api-url.com';

    return (
        <Router>
            <Routes>
                <Route path="/freelances" element={<FreelanceListPage />} />
                <Route path="/freelances/add" element={<FreelanceAddPage />} />
                <Route path="/freelances/edit/:id" element={<FreelanceEditPage />} />
                <Route path="/freelances/details/:id" element={<FreelanceDetailsPage />} />
                <Route path="/missions/:id" element={<MissionPage />} />

            </Routes>
        </Router>
    );
};

export default App;