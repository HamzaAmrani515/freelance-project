/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { Client } from '../models/Client';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class ClientControllerService {
    /**
     * @returns Client OK
     * @throws ApiError
     */
    public static findClientByNom({
        nom,
    }: {
        nom: string,
    }): CancelablePromise<Client> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/clients',
            query: {
                'nom': nom,
            },
        });
    }
}
