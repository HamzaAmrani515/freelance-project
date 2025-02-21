import React from 'react';
import { Freelancer } from '../api';
import { Link } from 'react-router-dom';

interface FreelanceTableProps {
    freelances: Freelancer[];
    onDelete: (id: number) => void;
}

const FreelanceTable: React.FC<FreelanceTableProps> = ({ freelances, onDelete }) => {
    return (
        <div className="overflow-x-auto shadow-lg rounded-lg bg-white p-4">
            <table className="min-w-full table-auto text-left">
                <thead className="bg-indigo-600 text-white">
                <tr>
                    <th className="px-6 py-3 text-sm font-semibold">Nom</th>
                    <th className="px-6 py-3 text-sm font-semibold">Prénom</th>
                    <th className="px-6 py-3 text-sm font-semibold">Email</th>
                    <th className="px-6 py-3 text-sm font-semibold">Actions</th>
                </tr>
                </thead>
                <tbody className="text-gray-700">
                {freelances.map((freelance) => (
                    <tr
                        key={freelance.id}
                        className="border-b hover:bg-gray-50 transition duration-300 ease-in-out"
                    >
                        <td className="px-6 py-4">{freelance.nom}</td>
                        <td className="px-6 py-4">{freelance.prenom}</td>
                        <td className="px-6 py-4">{freelance.email}</td>
                        <td className="px-6 py-4 flex space-x-3">
                            {/* View Button */}
                            <Link
                                to={`/freelances/details/${freelance.id}`}
                                className="bg-indigo-500 text-white py-2 px-4 rounded-md hover:bg-indigo-600 focus:outline-none focus:ring-2 focus:ring-indigo-400 transition duration-300 ease-in-out"
                            >
                                Afficher
                            </Link>

                            {/* Edit Button */}
                            <Link
                                to={`/freelances/edit/${freelance.id}`}
                                className="bg-teal-500 text-white py-2 px-4 rounded-md hover:bg-teal-600 focus:outline-none focus:ring-2 focus:ring-teal-400 transition duration-300 ease-in-out"
                            >
                                Modifier
                            </Link>

                            {/* Delete Button */}
                            <button
                                onClick={() => onDelete(freelance.id!)}
                                className="bg-red-500 text-white py-2 px-4 rounded-md hover:bg-red-600 focus:outline-none focus:ring-2 focus:ring-red-400 transition duration-300 ease-in-out"
                            >
                                Supprimer
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default FreelanceTable;