import React, { useEffect, useState } from 'react';
import FreelanceForm from '../components/FreelanceForm';
import { useNavigate, useParams } from 'react-router-dom';
import { toast } from "react-toastify";
import { FreelanceControllerService } from '../api/';
import { Freelance } from "../api/models/Freelance";

const FreelanceEditPage: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const [freelance, setFreelance] = useState<Freelance | null>(null);
    const [errorMessage, setErrorMessage] = useState<string | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchFreelance = async () => {
            try {
                const data = await FreelanceControllerService.getFreelanceById({ id: Number(id) });
                setFreelance(data);
            } catch (error: any) {
                setErrorMessage(error?.message || 'Erreur lors de la récupération du freelance.');
            }
        };

        fetchFreelance();
    }, [id]);

    useEffect(() => {
        if (errorMessage) {
            toast.error(errorMessage);
        }
    }, [errorMessage]);

    const handleEditFreelance = async (updatedFreelance: Freelance) => {
        if (freelance?.id) {
            try {
                await FreelanceControllerService.updateFreelance({ id: Number(id), requestBody: updatedFreelance });
                toast.success('Freelance mis à jour avec succès.');
                navigate('/freelances');
            } catch (error: any) {
                toast.error(error?.message || 'Erreur lors de la mise à jour du freelance.');
            }
        }
    };

    if (!freelance) return <div>Chargement...</div>;

    return (
        <div>
            <FreelanceForm onSubmit={handleEditFreelance} initialData={freelance} isUpdate={true} />
        </div>
    );
};

export default FreelanceEditPage;
