import {Injectable} from '@angular/core';
import {HttpClient, HttpParams, HttpResponse, HttpErrorResponse, HttpResponseBase} from "@angular/common/http";

@Injectable({
    providedIn: 'root'
})
export class AppService {

    constructor(private http: HttpClient) {
    }

    public handleSubmitClick(event: Event) {
        let clickedElement: Element = event.target as Element
        let endpointContainer: HTMLElement = clickedElement.closest('mat-expansion-panel')! as HTMLElement

        let queryParams = new HttpParams()
        for (let queryParam of endpointContainer.querySelectorAll('.query-param')) {
            let nameAndValue = this.parseElementValue(queryParam)
            queryParams = queryParams.set(nameAndValue.name, nameAndValue.value)
        }

        let requestBody: any = {};
        for (let bodyParam of endpointContainer.querySelectorAll('.body-param')) {
            let nameAndValue = this.parseElementValue(bodyParam)
            requestBody[nameAndValue.name] = nameAndValue.value
        }

        let responseContainer: HTMLElement = endpointContainer.querySelector('.response-container')!
        this.http.request<HttpResponse<any>>(
            endpointContainer.dataset['requestType']!,
            endpointContainer.dataset['requestMapping']!,
            {params: queryParams, body: requestBody, observe: 'response'}
        ).subscribe({
            next: (response: HttpResponse<any>) => {
                this.showResponse(responseContainer, response, response.body)
            },
            error: (err: HttpErrorResponse) => {
                this.showResponse(responseContainer, err, err.error)
            }
        });
    }

    private parseElementValue(element: Element) {
        let name: string, value: any
        if (element.tagName == 'INPUT') {
            let inputElement = element as HTMLInputElement
            name = inputElement.name
            if (inputElement.type == 'number') {
                value = inputElement.valueAsNumber
            } else {
                value = inputElement.value
            }
        } else if (element.tagName == 'MAT-CHECKBOX') {
            let inputElement = element.querySelector('input[type="checkbox"]') as HTMLInputElement
            name = element.getAttribute('name')!
            value = inputElement.checked
        } else {
            throw new Error('Trying to parse unknown element!\n' + JSON.stringify(element))
        }
        return {name: name, value: value}
    }

    private showResponse(responseContainer: HTMLElement, response: HttpResponseBase, body: any) {
        let responseElement: HTMLElement = document.createElement('div')
        responseElement.setAttribute('response-type', response.ok ? 'success' : 'error')
        responseElement.classList.add('response')

        let statusElement: HTMLElement = document.createElement('h4')
        statusElement.textContent = `${response.status} ${response.statusText}`

        let bodyElement: HTMLElement = document.createElement('div')
        bodyElement.classList.add('response-body')
        bodyElement.textContent += JSON.stringify(body, null, 2)

        responseContainer.innerHTML = ''
        responseContainer.appendChild(responseElement)
        responseElement.appendChild(statusElement)
        responseElement.appendChild(bodyElement)
    }
}