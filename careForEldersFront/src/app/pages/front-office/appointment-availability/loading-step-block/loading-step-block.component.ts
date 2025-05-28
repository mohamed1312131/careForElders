import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-loading-step-block',
  template: `
    <div class="loading__step" [ngClass]="{'loading__step--in': state !== 'waiting'}" [ngStyle]="style">
      <svg class="icon" [ngClass]="iconColor" width="24" height="24" aria-hidden="true">
        <use [attr.href]="iconHref" />
      </svg>
      <div>
        <div class="loading__step-title">{{title}}</div>
        <div class="loading__step-info">
          <ng-container *ngIf="start">{{ start | date:'medium' }} — </ng-container>
          <ng-container *ngIf="finish">{{ finish | date:'medium' }}</ng-container>
          <ng-container *ngIf="!finish && state === 'progress'"><span class="loading__ellipsis"><span class="loading__ellipsis-dot">.</span><span class="loading__ellipsis-dot">.</span><span class="loading__ellipsis-dot">.</span></span></ng-container>
        </div>
        <div class="loading__step-info" *ngIf="filesPrepared !== undefined && filesPreparedMax">
          {{ filesPrepared }} of {{ filesPreparedMax }} files préparés
        </div>
      </div>
    </div>
  `,
  styles: [`
    .icon {
      display: block;
      overflow: visible;
      width: 1.5em;
      height: 1.5em;
      transition: color 0.3s;
    }
    .icon--neutral { color: var(--neutral); }
    .icon--success { color: var(--success); }
    .icon--warning { color: var(--warning); }
  `]
})
export class LoadingStepBlockComponent {
  @Input() state: 'waiting' | 'progress' | 'done' = 'waiting';
  @Input() title?: string;
  @Input() distance: number = 0;
  @Input() fade: number = 0;
  @Input() start?: Date;
  @Input() finish?: Date;
  @Input() filesPrepared?: number;
  @Input() filesPreparedMax?: number;

  get iconHref() {
    if (this.state === 'waiting') return '#empty-circle';
    if (this.state === 'progress') return '#half-circle';
    return '#check-circle';
  }
  get iconColor() {
    if (this.state === 'waiting') return 'icon--neutral';
    if (this.state === 'progress') return 'icon--warning';
    return 'icon--success';
  }
  get style() {
    return {
      opacity: 1 - this.fade * 0.225,
      transform: `translateY(${100 * this.distance}%)`
    };
  }
}