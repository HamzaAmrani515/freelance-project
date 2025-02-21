import React, { useEffect, useState } from 'react';
import FreelanceTable from '../components/FreelanceTable';
import { toast } from "react-toastify";
import { Link } from "react-router-dom";
import { FreelanceControllerService } from '../api/';
import { Freelancer } from '../api/models/Freelancer';

const FreelanceListPage: React.FC = () => {
    const [freelancesList, setFreelancesList] = useState<Freelancer[]>([]);
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [errorMessage, setErrorMessage] = useState<string | null>(null);

    const fetchFreelances = async () => {
        try {
            setIsLoading(true);
            const freelances = await FreelanceControllerService.getAllFreelances();
            setFreelancesList(freelances);
        } catch (error: any) {
            setErrorMessage(error?.message || 'Erreur lors de la récupération des freelances.');
        } finally {
            setIsLoading(false);
        }
    };

    const handleDelete = async (id: number) => {
        try {
            await FreelanceControllerService.deleteFreelance({ id });
            toast.success('Freelance supprimé avec succès.');
            // Mise à jour de la liste après suppression
            setFreelancesList((prevList) => prevList.filter(freelance => freelance.id !== id));
        } catch (error: any) {
            toast.error(error?.message || 'Erreur lors de la suppression du freelance.');
        }
    };

    useEffect(() => {
        if (errorMessage) {
            toast.error(errorMessage);
        }
        fetchFreelances();
    }, [errorMessage]);

    if (isLoading) {
        return (
            <div className="flex justify-center items-center min-h-screen bg-gray-50">
                <div className="text-xl text-gray-500">Chargement...</div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50 py-8 px-4">
            <div className="max-w-7xl mx-auto space-y-8">
                {/* Header section */}
                <div className="flex justify-between items-center">
                    <h1 className="text-3xl font-semibold text-gray-800">Liste des Freelances liker </h1>
                    <Link to="/freelances/add">
                        <button
                            className="bg-indigo-600 text-white px-6 py-3 rounded-lg shadow-lg hover:bg-indigo-700 transition duration-200">
                            Ajouter un freelance
                        </button>
                    </Link>
                </div>

                {freelancesList.length === 0 ? (
                    <div className="text-center text-3xl font-semibold text-gray-800 py-10">
                        La liste des freelances est vide
                    </div>
                ) : (
                    <div className="overflow-hidden bg-white shadow-xl rounded-lg">
                        <FreelanceTable freelances={freelancesList} onDelete={handleDelete} />
                    </div>
                )}
            </div>
        </div>
    );
};

export default FreelanceListPage;
