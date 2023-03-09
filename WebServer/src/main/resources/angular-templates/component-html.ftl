<#-- @ftlvariable name="component" type="ru.vsu.csf.framework.frontend.UIComponent" -->

<#macro renderField uiField formName prefix>
    <#if uiField.getFieldType().name() == "TEXT" || uiField.getFieldType().name() == "NUMBER">
        <mat-form-field class="long-field">
            <mat-label>${uiField.getDisplayName()}</mat-label>
            <input matInput formControlName="${prefix}${uiField.getSubmitName()}" <#if uiField.getFieldType().name() == "NUMBER">type="number"</#if>>
        </mat-form-field>
    <#elseif uiField.getFieldType().name() == "BOOL">
        <mat-checkbox class="checkbox" formControlName="${prefix}${uiField.getSubmitName()}">${uiField.getDisplayName()}</mat-checkbox>
    <#elseif uiField.getFieldType().name() == "ENUM">
        <mat-form-field class="long-field">
            <mat-label>${uiField.getDisplayName()}</mat-label>
            <mat-select formControlName="${prefix}${uiField.getSubmitName()}">
                <#list uiField.getSubmitToDisplayValues() as submitName, displayName>
                <mat-option value="${submitName}">${displayName}</mat-option>
                </#list>
            </mat-select>
        </mat-form-field>
    </#if>
</#macro>

<mat-accordion class="generated-forms-container" multi>
<#list component.endpoints as endpoint>
<form [formGroup]="${endpoint.codeName()}Form"
      (ngSubmit)="submitForm(${endpoint.codeName()}Form)">
    <mat-expansion-panel>
        <mat-expansion-panel-header>
            <mat-panel-title>
                ${endpoint.requestType()}
            </mat-panel-title>
            <mat-panel-description>
                ${endpoint.displayName()}
            </mat-panel-description>
        </mat-expansion-panel-header>
        <div class="query-params">
            <#if endpoint.queryParams()?has_content>
                <h3 class="text-center">Query Params</h3>
            </#if>
            <#list endpoint.queryParams() as queryParam>
                <@renderField uiField=queryParam formName="${endpoint.codeName()}Form" prefix="query-"/>
            </#list>
        </div>
        <div class="request-body">
            <#if endpoint.requestBody()??>
                <h3 class="text-center">Request Body - ${endpoint.requestBody().entityName()}</h3>
                <#list endpoint.requestBody().fields() as requestField>
                   <@renderField uiField=requestField formName="${endpoint.codeName()}Form" prefix="body-"/>
                </#list>
            </#if>
        </div>
        <div class="request-footer">
            <button mat-raised-button [disabled]="${endpoint.codeName()}Form.disabled" color="primary" type="submit" class="float-right">
                <#if endpoint.requestType().name() == "POST">
                    Save
                <#elseif endpoint.requestType().name() == "GET">
                    Get
                <#elseif endpoint.requestType().name() == "PUT">
                    Change
                <#else>
                    Delete
                </#if>
            </button>
        </div>
        <app-response [response]="formToRequestDataMap.get(${endpoint.codeName()}Form).response">
        </app-response>
    </mat-expansion-panel>
</form>
</#list>
</mat-accordion>