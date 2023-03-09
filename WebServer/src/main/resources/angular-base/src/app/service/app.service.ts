import {Injectable} from '@angular/core';
import {HttpClient, HttpParams, HttpResponse} from "@angular/common/http";
import {Observable} from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class AppService {

    constructor(private http: HttpClient) {
    }

    public submitForm(requestType: string, mapping: string, values: any): Observable<HttpResponse<any>> {
        let queryParams = new HttpParams();
        let requestBody: any = {};
        for (const param of Object.entries(values)) {
            let paramName = param[0].split('-');
            if (paramName[0] == 'query') {
                queryParams = queryParams.set(paramName[1], param[1] as string);
            } else if (paramName[0] == 'body') {
                requestBody[paramName[1]] = param[1];
            }
        }
        return this.http.request<HttpResponse<any>>(
            requestType, mapping, {params: queryParams, body: requestBody, observe: 'response'}
        );
    }
}