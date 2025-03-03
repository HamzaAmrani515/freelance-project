import React, { useEffect, useState } from 'react';
import FreelanceGride from '../../components/freelance/FreelanceGride';
import { toast } from "react-toastify";
import { FreelancerRecommendationDTO, RecommendationControllerService } from '../../api';
import { useParams } from 'react-router-dom';

const FreelanceListPage: React.FC = () => {
    //useparam pour enlever ce qui est dans l url
    const { id } = useParams<{ id: string }>();
      //usestat pour detecter les changement dans la tablaux
    const [freelancesList, setFreelancesList] = useState<FreelancerRecommendationDTO[]>([]);
  
   
    const fetchFreelances = async () => {
      
        const freelances = await RecommendationControllerService.getRecommendations({ missionId: Number(id) });
        setFreelancesList(freelances);
    
    };

    

    useEffect(() => {
          fetchFreelances();
    });


    return (
        <div className="min-h-screen bg-gray-50 py-8 px-4">
            <div className="max-w-7xl mx-auto space-y-8">
               
            

                {freelancesList.length === 0 ? (
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
