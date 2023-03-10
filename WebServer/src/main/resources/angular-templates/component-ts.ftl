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
    ${endpoint.codeName()}Form: FormGroup;
    </#list>

    formToRequestDataMap: Map<FormGroup, any>;

<#macro renderFormCotrol uiField prefix>
    <#if uiField.getFieldType().name() == "CLASS">
        <#list uiField.getInnerFields() as innerField>
            <@renderFormCotrol uiField=innerField prefix="${prefix}${uiField.getSubmitName()}-"/>
        </#list>
    <#elseif uiField.getFieldType().name() == "LIST">
    <#else>
        "${prefix}${uiField.getSubmitName()}" : new FormControl(
        <#if uiField.getFieldType().name() == "BOOL">false<#else>null</#if><#if uiField.isRequired()>,Validators.required</#if>
        ),
    </#if>

</#macro>

    constructor(private appService: AppService) {
    <#list component.endpoints as endpoint>
        this.${endpoint.codeName()}Form = new FormGroup({
        <#list endpoint.queryParams() as queryParam>
            <@renderFormCotrol uiField=queryParam prefix="query-"/>
        </#list>
        <#if endpoint.requestBody()??>
        <#list endpoint.requestBody().fields() as bodyField>
            <@renderFormCotrol uiField=bodyField prefix="body-"/>
        </#list>
        </#if>
        });
    </#list>
        this.formToRequestDataMap = new Map<FormGroup, any>([
        <#list component.endpoints as endpoint>
            [this.${endpoint.codeName()}Form, {mapping: '${endpoint.mapping()}', requestType: '${endpoint.requestType().name()}', response: null}],
        </#list>
        ]);
    }

    ngOnInit(): void {
    }

    ngOnDestroy(): void {
    }

    <#list component.endpoints as endpoint>
    get ${endpoint.codeName()}FormControls(): any {
        return this.${endpoint.codeName()}Form['controls'];
    }
    </#list>


    submitForm(form: FormGroup): void {
        if (form.invalid) {
            return;
        }
        let requestData = this.formToRequestDataMap.get(form);
        form.disable();
        this.appService.submitForm(requestData.requestType, requestData.mapping, form.value).subscribe({
            next: (response: HttpResponse<any>) => {
                requestData.response = response;
                form.enable();
            },
            error: (err: HttpErrorResponse) => {
                requestData.response = err;
                form.enable();
            }
        })
    }
}