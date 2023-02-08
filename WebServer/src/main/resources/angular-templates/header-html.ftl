<#-- @ftlvariable name="components" type="java.util.List<ru.vsu.csf.framework.frontend.UIComponent>" -->
<mat-toolbar color="primary">
    <#list components as component>
    <a mat-button routerLink="/${component.fileName}"
       routerLinkActive="active-header-link"
       [routerLinkActiveOptions]="{exact: true}">${component.name}</a>
    </#list>
    <span class="header-spacer"></span>
    <a mat-button routerLink=""
       routerLinkActive="active-header-link"
       [routerLinkActiveOptions]="{exact: true}">Info</a>
</mat-toolbar>