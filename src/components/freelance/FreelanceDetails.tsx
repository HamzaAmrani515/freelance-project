import React, { useEffect, useState } from 'react';
import { Freelancer, FreelanceControllerService } from '../../api';
import { useNavigate, useParams } from 'react-router-dom';

const FreelanceDetails: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const [freelance, setFreelance] = useState<Freelancer | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchFreelance = async () => {
            try {
                const data = await FreelanceControllerService.getFreelanceById({ id: Number(id) });
                setFreelance(data);
            } catch (error) {
                console.error("Error fetching freelance details:", error);
            }
        };

        fetchFreelance();
    }, [id]);

    if (!freelance) return (
        <div className="flex justify-center items-center min-h-screen bg-gray-50">
            <div className="text-xl text-gray-500">Chargement...</div>
        </div>
    );

    return (
        <div className="max-w-4xl mx-auto p-8 bg-white rounded-lg shadow-xl space-y-6">
            <div className="flex justify-between items-center">
                <h2 className="text-3xl font-semibold text-gray-800">{freelance.nom} {freelance.prenom}</h2>
            </div>

            <p className="mt-2 text-xl text-gray-600">{freelance.email}</p>

            <div className="mt-6 space-y-4">
                <div className="flex items-center space-x-3">
                    <span className="font-medium text-gray-600">Nom:</span>
                    <span className="text-gray-800">{freelance.nom}</span>
                </div>
                <div className="flex items-center space-x-3">
                    <span className="font-medium text-gray-600">Prénom:</span>
                    <span className="text-gray-800">{freelance.prenom}</span>
                </div>
                <div className="flex items-center space-x-3">
                    <span className="font-medium text-gray-600">Email:</span>
                    <span className="text-gray-800">{freelance.email}</span>
                </div>
            </div>

            <div className="mt-6 flex justify-center">
                <button
                    onClick={() => navigate('/freelances')}
                    className="px-6 py-3 bg-indigo-600 text-white font-semibold rounded-md shadow-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 transition duration-200"
                >
                    Fermer
                </button>
            </div>
        </div>
    );
};

export default FreelanceDetails;