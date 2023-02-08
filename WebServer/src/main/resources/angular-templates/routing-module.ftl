<#-- @ftlvariable name="components" type="java.util.List<ru.vsu.csf.framework.frontend.UIComponent>" -->
import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {InfoComponent} from './info/info.component';
<#list components as component>
import {${component.scriptName}Component} from "./${component.fileName}/${component.fileName}.component";
</#list>

const routes: Routes = [
    {path: '', component: InfoComponent},
<#list components as component>
    {path: '${component.fileName}', component: ${component.scriptName}Component},
</#list>
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {
}

export const routingComponents = [
    InfoComponent, <#list components as component>${component.scriptName}Component, </#list>
]