/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { FreelancerRecommendationDTO } from '../models/FreelancerRecommendationDTO';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class RecommendationControllerService {
    /**
     * @returns FreelancerRecommendationDTO OK
     * @throws ApiError
     */
    public static getRecommendations({
        missionId,
    }: {
        missionId: number,
    }): CancelablePromise<Array<FreelancerRecommendationDTO>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/recommandations/mission/{missionId}',
            path: {
                'missionId': missionId,
            },
        });
    }
}
