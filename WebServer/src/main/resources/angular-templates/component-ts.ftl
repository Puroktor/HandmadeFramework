<#-- @ftlvariable name="component" type="ru.vsu.csf.framework.frontend.UIComponent" -->
import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormGroup, FormControl, Validators} from '@angular/forms';
import {HttpResponse, HttpErrorResponse} from "@angular/common/http";
import {AppService} from '../service/app.service';

@Component({
    selector: 'app-${component.fileName}',
    templateUrl: './${component.fileName}.component.html',
    styleUrls: ['./${component.fileName}.component.css']
})
export class ${component.scriptName}Component implements OnInit, OnDestroy {

    <#list component.endpoints as endpoint>
    ${endpoint.codeName()}Form: FormGroup
    </#list>

    formToRequestDataMap: Map<FormGroup, any>

    constructor(private appService: AppService) {
    <#list component.endpoints as endpoint>
        this.${endpoint.codeName()}Form = new FormGroup({
        <#list endpoint.queryParams() as queryParam>
            "query-${queryParam.submitName()}" : new FormControl(null<#if queryParam.required>, Validators.required</#if>),
        </#list>
        <#if endpoint.requestBody()??>
        <#list endpoint.requestBody().fields() as bodyField>
            "body-${bodyField.submitName()}" : new FormControl(null<#if bodyField.required>, Validators.required</#if>),
        </#list>
        </#if>
        })
    </#list>
        this.formToRequestDataMap = new Map<FormGroup, any>([
        <#list component.endpoints as endpoint>
            [this.${endpoint.codeName()}Form, {mapping: '${endpoint.mapping()}', requestType: '${endpoint.requestType().name()}', response: null}],
        </#list>
        ])
    }

    ngOnInit(): void {
    }

    ngOnDestroy(): void {
    }

    <#list component.endpoints as endpoint>
    get ${endpoint.codeName()}FormControls(): any {
        return this.${endpoint.codeName()}Form['controls']
    }
    </#list>


    submitForm(form: FormGroup): void {
        if (form.invalid) {
            return
        }
        let requestData = this.formToRequestDataMap.get(form)
        this.appService.submitForm(requestData.requestType, requestData.mapping, form.value).subscribe({
            next: (response: HttpResponse<any>) => {
                requestData.response = response
            },
            error: (err: HttpErrorResponse) => {
                requestData.response = err
            }
        })
    }
}