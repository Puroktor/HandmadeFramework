import {Component, Input, OnInit, OnDestroy} from '@angular/core';
import {HttpResponseBase} from "@angular/common/http";

@Component({
    selector: 'app-response',
    templateUrl: './response.component.html',
    styleUrls: ['./response.component.css']
})
export class ResponseComponent implements OnInit, OnDestroy {

    @Input() response!: HttpResponseBase | null;

    ngOnInit(): void {
    }

    ngOnDestroy(): void {
    }
}