import React, { useState } from 'react';
import { Freelance } from '../api';
import { useNavigate } from 'react-router-dom';

interface FreelanceFormProps {
    onSubmit: (freelance: Freelance) => void;
    initialData?: Partial<Freelance>;
    isUpdate: boolean;
}

const FreelanceForm: React.FC<FreelanceFormProps> = ({ onSubmit, initialData = {}, isUpdate }) => {
    const [freelance, setFreelance] = useState<Partial<Freelance>>({
        nom: initialData.nom || '',
        prenom: initialData.prenom || '',
        email: initialData.email || '',
    });

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setFreelance((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (freelance.nom && freelance.prenom && freelance.email) {
            console.log(freelance)
            onSubmit(freelance as Freelance); // S'assurer que les valeurs sont complètes
        } else {
            alert('Tous les champs sont obligatoires.');
        }
    };

    return (
        <div className="max-w-lg mx-auto bg-white p-8 rounded-lg shadow-xl space-y-6">
            <h2 className="text-2xl font-semibold text-gray-800 text-center">
                {isUpdate ? 'Modifier le Freelance' : 'Ajouter un Freelance'}
            </h2>

            <form onSubmit={handleSubmit} className="space-y-6">
                <div>
                    <input
                        type="text"
                        name="nom"
                        value={freelance.nom}
                        onChange={handleChange}
                        placeholder="Nom"
                        className="w-full p-4 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
                        required
                    />
                </div>

                <div>
                    <input
                        type="text"
                        name="prenom"
                        value={freelance.prenom}
                        onChange={handleChange}
                        placeholder="Prénom"
                        className="w-full p-4 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
                        required
                    />
                </div>

                <div>
                    <input
                        type="email"
                        name="email"
                        value={freelance.email}
                        onChange={handleChange}
                        placeholder="Email"
                        className="w-full p-4 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
                        required
                    />
                </div>

                <button
                    type="submit"
                    className="w-full py-4 bg-indigo-600 text-white rounded-md shadow-lg hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
                >
                    {isUpdate ? 'Mettre à jour' : 'Sauvegarder'}
                </button>
            </form>
        </div>
    );
};

export default FreelanceForm;
