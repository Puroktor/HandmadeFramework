<#-- @ftlvariable name="endpoint" type="ru.vsu.csf.framework.frontend.UIEndpoint" -->
<#-- @ftlvariable name="componentName" type="java.lang.String" -->
import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormGroup, FormControl, FormArray, Validators} from '@angular/forms';
import {HttpResponse, HttpErrorResponse} from "@angular/common/http";
import {AppService} from '../../service/app.service';

@Component({
    selector: 'app-${componentName}-${endpoint.getFileName()}',
    templateUrl: './${endpoint.getFileName()}.component.html',
    styleUrls: ['./${endpoint.getFileName()}.component.css']
})
export class ${endpoint.getScriptName()}Component implements OnInit, OnDestroy {

<#macro renderFormCotrol uiField prefix>
    <#if uiField.getFieldType().name() == "CLASS">
        <#list uiField.getInnerFields() as innerField>
            <@renderFormCotrol uiField=innerField prefix="${prefix}${uiField.getSubmitName()}-"/>
        </#list>
    <#elseif uiField.getFieldType().name() == "LIST">
<#--        "${prefix}${uiField.getSubmitName()}" : new FormArray([]),-->
    <#else>
        "${prefix}${uiField.getSubmitName()}" : new FormControl(
        <#if uiField.getFieldType().name() == "BOOL">false<#else>null</#if><#if uiField.isRequired()>,Validators.required</#if>
        ),
    </#if>
</#macro>

    private mapping: string = '${endpoint.getMapping()}';
    private requestType: string = '${endpoint.getRequestType().name()}';
    formGroup: FormGroup;
    response: any|null = null;

    constructor(private appService: AppService) {
        this.formGroup = new FormGroup({
        <#list endpoint.getQueryParams() as queryParam>
            <@renderFormCotrol uiField=queryParam prefix="query-"/>
        </#list>
        <#if endpoint.getRequestBody()??>
        <#list endpoint.getRequestBody().getFields() as bodyField>
            <@renderFormCotrol uiField=bodyField prefix="body-"/>
        </#list>
        </#if>
        });
    }

    ngOnInit(): void {
    }

    ngOnDestroy(): void {
    }

    submitForm(): void {
        if (this.formGroup.invalid) {
            return;
        }
        this.formGroup.disable();
        this.appService.submitForm(this.requestType, this.mapping, this.formGroup.value).subscribe({
            next: (response: HttpResponse<any>) => {
                this.response = response;
                this.formGroup.enable();
            },
            error: (err: HttpErrorResponse) => {
                this.response = err;
                this.formGroup.enable();
            }
        })
    }
}