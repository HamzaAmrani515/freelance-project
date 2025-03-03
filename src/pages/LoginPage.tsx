import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { ClientControllerService } from '../api'; 

const LoginPage: React.FC = () => {
    const navigate = useNavigate();

    const [credentials, setCredentials] = useState({
        name: "",
        password: "",
    });

    const [loading, setLoading] = useState(false);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setCredentials({ ...credentials, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);

        try {
            const response = await ClientControllerService.findClientByNom({nom: credentials.name});
            if (response) {
                toast.success("Connexion réussie !");
                navigate(`/clients/${response.nom}`);
            } else {
                toast.error("Identifiants incorrects.");
            }
        } catch (error) {
            toast.error("Erreur d'authentification.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-100">
            <div className="bg-white p-8 shadow-lg rounded-lg w-96">
                <h2 className="text-2xl font-semibold text-center mb-4">Connexion</h2>
                <form onSubmit={handleSubmit} className="space-y-4">
                    <input
                        type="text"
                        name="name"
                        placeholder="Nom"
                        value={credentials.name}
                        onChange={handleChange}
                        required
                        className="w-full px-4 py-2 border rounded-md"
                    />
                    <input
                        type="password"
                        name="password"
                        placeholder="Mot de passe"
                        value={credentials.password}
                        onChange={handleChange}
                        required
                        className="w-full px-4 py-2 border rounded-md"
                    />
                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full bg-blue-500 hover:bg-blue-600 text-white py-2 rounded-md"
                    >
                        {loading ? "Connexion..." : "Se connecter"}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default LoginPage;
