<#-- @ftlvariable name="component" type="ru.vsu.csf.framework.frontend.UIComponent" -->
import {Component, OnDestroy, OnInit} from '@angular/core';

@Component({
    selector: 'app-${component.fileName}',
    templateUrl: './${component.fileName}.component.html',
    styleUrls: ['./${component.fileName}.component.css']
})
export class ${component.scriptName}Component implements OnInit, OnDestroy {

    ngOnInit(): void {
    }

    ngOnDestroy(): void {
    }
}