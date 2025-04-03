import React, { useEffect, useState } from 'react';
import FreelanceGride from '../../components/freelance/FreelanceGride';
import { FreelancerRecommendationDTO, RecommendationControllerService } from '../../api';
import { useParams } from 'react-router-dom';

const FreelanceListPage: React.FC = () => {
    const { id } = useParams<{ id: string }>();

    const [freelancesList, setFreelancesList] = useState<FreelancerRecommendationDTO[]>([]);
    const [loading, setLoading] = useState<boolean>(true);

    useEffect(() => {
        const fetchFreelances = async () => {
            setLoading(true);
            try {
                const freelances = await RecommendationControllerService.getRecommendations({
                    missionId: Number(id)
                });
                setFreelancesList(freelances);
            } catch (error) {
                console.error("Erreur lors de la récupération des freelances :", error);
            } finally {
                setLoading(false);
            }
        };

        fetchFreelances();
    }, [id]);

    return (
        <div className="min-h-screen bg-gray-50 py-8 px-4">
            <div className="max-w-7xl mx-auto space-y-8">
                {loading ? (
                    <div className="text-center text-2xl font-semibold text-gray-600 py-10">
                        Chargement en cours...
                    </div>
                ) : freelancesList.length === 0 ? (
                    <div className="text-center text-3xl font-semibold text-gray-800 py-10">
                        La liste des freelances est vide
                    </div>
                ) : (
                    <div className="overflow-hidden bg-white shadow-xl rounded-lg">
                        <FreelanceGride freelances={freelancesList} />
                    </div>
                )}
            </div>
        </div>
    );
};

export default FreelanceListPage;
