<#-- @ftlvariable name="component" type="ru.vsu.csf.framework.frontend.UIComponent" -->
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
        <#if queryParam.field().type().name() == "TEXT">
            <mat-form-field class="long-field">
                <mat-label>${queryParam.field().name()}</mat-label>
                <input class="query-param" matInput data-param-name="${queryParam.submitName()}">
            </mat-form-field>
        </#if>
        </#list>
    </div>
    <div class="request-body">
        <#if endpoint.requestBody()??>
            <h3 class="text-center">Request Body - ${endpoint.requestBody().entityName()}</h3>
            <#list endpoint.requestBody().fields() as requestField>
                <#if requestField.type().name() == "TEXT">
                    <mat-form-field class="long-field">
                        <mat-label>${requestField.name()}</mat-label>
                        <input class="body-param" matInput name="${requestField.name()}">
                    </mat-form-field>
                </#if>
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