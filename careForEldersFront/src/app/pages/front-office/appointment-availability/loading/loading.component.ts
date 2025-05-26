import { Component, Input, OnInit } from '@angular/core';


type LoadingState = 'waiting' | 'progress' | 'done';
interface LoadingStep {
  id: number;
  state: LoadingState;
  title?: string;
  start?: Date;
  finish?: Date;
  filesPrepared?: number;
  filesPreparedMax?: number;
}
@Component({
  selector: 'app-loading',
  templateUrl: './loading.component.html',
  styleUrl: './loading.component.scss'
})
export class LoadingComponent implements OnInit {
  @Input() steps: LoadingStep[] = [
    { id: 0, state: 'waiting', title: 'Préparation' },
    { id: 1, state: 'waiting', title: 'Vérification des disponibilités', filesPreparedMax: 50 },
    { id: 2, state: 'waiting', title: 'Confirmation', filesPreparedMax: 50 },
    { id: 3, state: 'waiting', title: 'Création du rendez-vous' },
    { id: 4, state: 'waiting', title: 'Finalisation' }
  ];
  stepObjects: LoadingStep[] = [];
  step = 0;
  allStepsDone = false;
  rectAngles = Array.from({length: 16}, (_, r) => 360 / 16 * r);

  ngOnInit() {
    this.stepObjects = this.steps.map(step => ({ ...step }));
    this.animateSteps();
  }

  animateSteps() {
    if (this.step >= this.steps.length) {
      this.allStepsDone = true;
      return;
    }
    setTimeout(() => {
      const updated = this.updateStep(this.stepObjects[this.step]);
      this.stepObjects[this.step] = {...this.stepObjects[this.step], ...updated};
      if (updated.state === 'done') {
        this.step++;
        this.animateSteps();
      } else if (updated.state === 'progress' && updated.filesPreparedMax) {
        // Increment file preparation
        setTimeout(() => {
          this.stepObjects[this.step] = {
            ...this.stepObjects[this.step],
            filesPrepared: (this.stepObjects[this.step].filesPrepared || 0) + 1
          };
          this.animateSteps();
        }, 20);
      } else {
        this.animateSteps();
      }
    }, this.step === 1 || this.step === 2 ? 20 : 700);
  }

  updateStep(item: LoadingStep): LoadingStep {
    const { state, start, filesPrepared, filesPreparedMax } = item;
    const updated: LoadingStep = { ...item };
    if (!start) {
      updated.start = new Date();
      updated.state = 'progress';
      if (!filesPreparedMax) return updated;
    }
    if (filesPreparedMax) {
      updated.filesPrepared = Math.min((filesPrepared ?? -1) + 1, filesPreparedMax);
    }
    if (!filesPreparedMax || updated.filesPrepared === filesPreparedMax) {
      updated.finish = new Date();
      updated.state = 'done';
    }
    return updated;
  }

  getDistance(i: number) {
    if (this.allStepsDone) {
      return i - Math.floor(this.steps.length / 2);
    } else {
      let moveBy = this.step;
      if (i > this.step + 1) {
        const stepHeight = 5.25;
        moveBy += (i - (this.step + 1)) * (1.5 / stepHeight);
      }
      return i - moveBy;
    }
  }
  getFade(i: number) {
    return this.allStepsDone ? 0 : Math.abs(i - this.step);
  }
}
