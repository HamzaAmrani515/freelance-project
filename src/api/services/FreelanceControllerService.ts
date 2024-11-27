/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { Freelance } from '../models/Freelance';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class FreelanceControllerService {
    /**
     * @returns Freelance OK
     * @throws ApiError
     */
    public static getFreelanceById({
        id,
    }: {
        id: number,
    }): CancelablePromise<Freelance> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/freelances/{id}',
            path: {
                'id': id,
            },
        });
    }
    /**
     * @returns Freelance OK
     * @throws ApiError
     */
    public static updateFreelance({
        id,
        requestBody,
    }: {
        id: number,
        requestBody: Freelance,
    }): CancelablePromise<Freelance> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/freelances/{id}',
            path: {
                'id': id,
            },
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @returns any OK
     * @throws ApiError
     */
    public static deleteFreelance({
        id,
    }: {
        id: number,
    }): CancelablePromise<any> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/freelances/{id}',
            path: {
                'id': id,
            },
        });
    }
    /**
     * @returns Freelance OK
     * @throws ApiError
     */
    public static getAllFreelances(): CancelablePromise<Array<Freelance>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/freelances',
        });
    }
    /**
     * @returns Freelance OK
     * @throws ApiError
     */
    public static saveFreelance({
        requestBody,
    }: {
        requestBody: Freelance,
    }): CancelablePromise<Freelance> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/freelances',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
}
