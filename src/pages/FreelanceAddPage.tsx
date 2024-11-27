import React, { useEffect, useState } from 'react';
import FreelanceForm from '../components/FreelanceForm';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { FreelanceControllerService } from '../api/';
import { Freelance } from '../api/models/Freelance';

const FreelanceAddPage: React.FC = () => {
    const [errorMessage, setErrorMessage] = useState<string | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        if (errorMessage) {
            toast.error(errorMessage);
        }
    }, [errorMessage]);

    const handleAddFreelance = async (newFreelance: Freelance) => {
        try {
            console.log(newFreelance);
            await FreelanceControllerService.saveFreelance({ requestBody: newFreelance });
            navigate('/freelances');
        } catch (error: any) {
            setErrorMessage(error?.message || 'Erreur lors de l’ajout du freelance.');
        }
    };
    
    return (
        <div>
            <FreelanceForm 
                onSubmit={handleAddFreelance} 
                initialData={{ nom: '', prenom: '', email: '' }} 
                isUpdate={false} 
            />
        </div>
    );
};

export default FreelanceAddPage;
