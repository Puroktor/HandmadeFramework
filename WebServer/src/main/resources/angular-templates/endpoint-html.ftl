<#-- @ftlvariable name="endpoint" type="ru.vsu.csf.framework.frontend.UIEndpoint" -->

<#macro renderField uiField prefix>
    <#if uiField.getFieldType().name() == "TEXT" || uiField.getFieldType().name() == "NUMBER" ||  uiField.getFieldType().name() == "ENUM">
        <mat-form-field class="long-field">
            <mat-label>${uiField.getDisplayName()}</mat-label>
            <#if uiField.getFieldType().name() == "ENUM">
            <mat-select formControlName="${prefix}${uiField.getSubmitName()}">
                <#list uiField.getSubmitToDisplayValues() as submitName, displayName>
                    <mat-option value="${submitName}">${displayName}</mat-option>
                </#list>
            </mat-select>
            <#else>
            <input matInput formControlName="${prefix}${uiField.getSubmitName()}" <#if uiField.getFieldType().name() == "NUMBER">type="number"</#if>>
            </#if>
            <mat-error *ngIf="formGroup.controls['${prefix}${uiField.getSubmitName()}'].errors?.['required']">${uiField.getDisplayName()} is required</mat-error>
        </mat-form-field>
    <#elseif uiField.getFieldType().name() == "BOOL">
        <mat-checkbox class="checkbox" formControlName="${prefix}${uiField.getSubmitName()}">${uiField.getDisplayName()}</mat-checkbox>
    <#elseif uiField.getFieldType().name() == "LIST">
<#--        <div class="array-container" formArrayName="${prefix}${uiField.getSubmitName()}">-->
<#--            <div class="form-group">-->
<#--                <mat-form-field class="long-field">-->
<#--                    <mat-label>Element {{i}}</mat-label>-->
<#--                    <input matInput formControlName="${prefix}${uiField.getSubmitName()}{{i}}">-->
<#--                </mat-form-field>-->
<#--            </div>-->
<#--            <button mat-fab color="primary">-->
<#--                <mat-icon>delete</mat-icon>-->
<#--            </button>-->
<#--        </div>-->
    <#elseif uiField.getFieldType().name() == "CLASS">
        <div class="class-field-container">
            <p class="label">${uiField.getDisplayName()}:</p>
            <div class="class-container">
                <#list uiField.getInnerFields() as innerField>
                    <@renderField uiField=innerField prefix="${prefix}${uiField.getSubmitName()}-"/>
                </#list>
            </div>
        </div>
    </#if>
</#macro>

<form [formGroup]="formGroup"
      (ngSubmit)="submitForm()">
    <mat-expansion-panel>
        <mat-expansion-panel-header>
            <mat-panel-title>
                ${endpoint.getRequestType()}
            </mat-panel-title>
            <mat-panel-description>
                ${endpoint.getDisplayName()}
            </mat-panel-description>
        </mat-expansion-panel-header>
        <div class="query-params">
            <#if endpoint.getQueryParams()?has_content>
                <h3 class="text-center">Query Params</h3>
            </#if>
            <#list endpoint.getQueryParams() as queryParam>
                <@renderField uiField=queryParam prefix="query-"/>
            </#list>
        </div>
        <div class="request-body">
            <#if endpoint.getRequestBody()??>
                <h3 class="text-center">Request Body - ${endpoint.getRequestBody().getEntityName()}</h3>
                <#list endpoint.getRequestBody().getFields() as requestField>
                   <@renderField uiField=requestField prefix="body-"/>
                </#list>
            </#if>
        </div>
        <div class="request-footer">
            <button mat-raised-button [disabled]="formGroup.disabled" color="primary" type="submit" class="float-right">
                <#if endpoint.getRequestType().name() == "POST">
                    Save
                <#elseif endpoint.getRequestType().name() == "GET">
                    Get
                <#elseif endpoint.getRequestType().name() == "PUT">
                    Change
                <#else>
                    Delete
                </#if>
            </button>
        </div>
        <app-response [response]="response">
        </app-response>
    </mat-expansion-panel>
</form>