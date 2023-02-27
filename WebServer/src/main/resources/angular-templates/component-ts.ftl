<#-- @ftlvariable name="component" type="ru.vsu.csf.framework.frontend.UIComponent" -->
import {Component, OnDestroy, OnInit} from '@angular/core';
import {AppService} from '../service/app.service';

@Component({
    selector: 'app-${component.fileName}',
    templateUrl: './${component.fileName}.component.html',
    styleUrls: ['./${component.fileName}.component.css']
})
export class ${component.scriptName}Component implements OnInit, OnDestroy {

    constructor(private appService: AppService) {
    }

    ngOnInit(): void {
    }

    ngOnDestroy(): void {
    }


    handleSubmitClick(event: Event) {
        this.appService.handleSubmitClick(event)
    }
}