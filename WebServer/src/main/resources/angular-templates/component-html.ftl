<#-- @ftlvariable name="component" type="ru.vsu.csf.framework.frontend.UIComponent" -->

<#macro renderField uiField class>
    <#if uiField.type().name() == "TEXT" || uiField.type().name() == "NUMBER">
        <mat-form-field class="long-field">
            <mat-label>${uiField.displayName()}</mat-label>
            <input class="${class}" matInput name="${uiField.submitName()}" <#if uiField.type().name() == "NUMBER">type="number"</#if>>
        </mat-form-field>
    <#elseif uiField.type().name() == "BOOL">
        <mat-checkbox class="${class} checkbox" name="${uiField.submitName()}">${uiField.displayName()}</mat-checkbox>
    </#if>
</#macro>

<mat-accordion class="generated-forms-container" multi>
<#list component.endpoints as endpoint>
<mat-expansion-panel data-request-type="${endpoint.requestType().name()}"
                     data-request-mapping="${endpoint.mapping()}">
    <mat-expansion-panel-header>
        <mat-panel-title>
            ${endpoint.requestType()}
        </mat-panel-title>
        <mat-panel-description>
            ${endpoint.name()}
        </mat-panel-description>
    </mat-expansion-panel-header>
    <div class="query-params">
        <#if endpoint.queryParams()?has_content>
            <h3 class="text-center">Query Params</h3>
        </#if>
        <#list endpoint.queryParams() as queryParam>
            <@renderField uiField=queryParam class="query-param"/>
        </#list>
    </div>
    <div class="request-body">
        <#if endpoint.requestBody()??>
            <h3 class="text-center">Request Body - ${endpoint.requestBody().entityName()}</h3>
            <#list endpoint.requestBody().fields() as requestField>
               <@renderField uiField=requestField class="body-param"/>
            </#list>
        </#if>
    </div>
    <div class="request-footer">
        <button mat-raised-button color="primary" type="button" class="float-right"
                (click)="handleSubmitClick($event)">
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
    <div class="response-container">
    </div>
</mat-expansion-panel>
</#list>
</mat-accordion>