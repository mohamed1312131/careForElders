// events-list.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Event, EventDTO, User } from 'src/app/models/Event';
import { EventService } from 'src/app/services/event.service';
import { UserService } from 'src/app/services/user.service';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators, ValidatorFn, AbstractControl, ValidationErrors } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectChange, MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-events-list',
  standalone: true,
  imports: [
    CommonModule, 
    RouterModule,
    FormsModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatIconModule,
    MatSelectModule,
    MatCheckboxModule
  ],
  templateUrl: './events-list.component.html',
  styleUrl: './events-list.component.scss'
})
export class EventsListComponent implements OnInit {
  events: Event[] = [];
  isLoading = true;
  error: string | null = null;
  allUsers: User[] = [];
    currentUser: any = null;
    currentEvent: Event | null = null;
  // Modal states
  showCreateModal = false;
  selectedParticipants: string[] = [];
  showDetailsModal = false;
  // Form
  eventForm: FormGroup;
showUpdateModal = false;
selectedParticipantsForUpdate: string[] = [];
updateEventForm: FormGroup;
  constructor(
    private eventService: EventService,
    private userService: UserService,
    private fb: FormBuilder,
    private dialog: MatDialog
  ) {
this.eventForm = this.fb.group({
  title: ['', [
    Validators.required,
    Validators.minLength(3),
    Validators.maxLength(100)
  ]],
  date: ['', [
    Validators.required,
    this.futureDateValidator()
  ]],
  location: ['', [
    Validators.required,
    Validators.minLength(3),
    Validators.maxLength(100)
  ]],
  description: ['', [
    Validators.maxLength(500)
  ]],
  participants: [[]]
});
this.updateEventForm = this.fb.group({
  title: ['', [
    Validators.required,
    Validators.minLength(3),
    Validators.maxLength(100)
  ]],
  date: ['', [
    Validators.required,
    this.futureDateValidator()
  ]],
  location: ['', [
    Validators.required,
    Validators.minLength(3),
    Validators.maxLength(100)
  ]],
  description: ['', [
    Validators.maxLength(500)
  ]],
  participants: [[]]
});
  }
futureDateValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const selectedDate = new Date(control.value);
    const today = new Date();
    today.setHours(0, 0, 0, 0); // Reset time part
    
    return selectedDate < today ? { pastDate: true } : null;
  };
}
  ngOnInit(): void {
    this.loadEvents();
    this.loadUsers();
     this.loadCurrentUser();
  }

  loadEvents(): void {
    this.isLoading = true;
    this.error = null;
    
    this.eventService.getAllEvents().subscribe({
      next: (events: Event[]) => {
        this.events = events;
        this.isLoading = false;
      },
      error: (err) => {
        this.error = 'Failed to load events. Please try again later.';
        this.isLoading = false;
        console.error('Error loading events:', err);
      }
    });
  }

loadUsers(): void {
  this.userService.getAllUsers().subscribe({
    next: (users: any) => {
      this.allUsers = users.map((user: any) => ({
        id: user._id || user.id,
        name: `${user.firstName} ${user.lastName}`,
        role: user.role || 'Participant' // Default to 'Participant' if role doesn't exist
      }));
    },
    error: (err) => {
      console.error('Error loading users:', err);
    }
  });
}
getUserById(userId: string): User | undefined {
  return this.allUsers.find(u => u.id === userId);
}
  openCreateModal(): void {
    this.showCreateModal = true;
    this.eventForm.reset();
    this.selectedParticipants = [];
  }

openUpdateModal(event: Event): void {
  this.currentEvent = event;
  this.selectedParticipantsForUpdate = [...event.participantIds];
  this.showUpdateModal = true;
  
  this.updateEventForm.patchValue({
    title: event.title,
    date: this.formatDateForInput(new Date(event.date)),
    location: event.location,
    description: event.description,
    participants: [...event.participantIds] // Initialize form control with current participants
  });
}

onUpdateSubmit(): void {
  // Mark all fields as touched to show validation messages
  this.updateEventForm.markAllAsTouched();
  
  if (this.updateEventForm.invalid || !this.currentEvent) {
    return;
  }

  const currentParticipants = this.selectedParticipantsForUpdate;
  const originalParticipants = this.currentEvent.participantIds;

  const eventData: EventDTO = {
    ...this.updateEventForm.value,
    participantIds: currentParticipants
  };

  this.eventService.updateEvent(this.currentEvent.id!, eventData).subscribe({
    next: (updatedEvent) => {
      const participantsToAdd = currentParticipants.filter(
        id => !originalParticipants.includes(id)
      );
      const participantsToRemove = originalParticipants.filter(
        id => !currentParticipants.includes(id)
      );

      const addOperations = participantsToAdd.map(userId => 
        this.eventService.addParticipant(updatedEvent.id!, userId)
      );
      const removeOperations = participantsToRemove.map(userId =>
        this.eventService.removeParticipant(updatedEvent.id!, userId)
      );

      // If no participant changes needed, just close and reload
      if (addOperations.length === 0 && removeOperations.length === 0) {
        this.loadEvents();
        this.closeModal();
        return;
      }

      forkJoin([...addOperations, ...removeOperations]).subscribe({
        next: () => {
          this.loadEvents();
          this.closeModal();
        },
        error: (err) => {
          console.error('Error updating participants:', err);
          this.loadEvents();
          this.closeModal();
        }
      });
    },
    error: (err) => {
      console.error('Error updating event:', err);
      this.closeModal(); // Close modal even if there's an error
    }
  });
}
private formatDateForInput(date: Date): string {
  const pad = (num: number) => num.toString().padStart(2, '0');
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
}

closeModal(): void {
  this.showDetailsModal = false;
  this.showCreateModal = false;
  this.showUpdateModal = false;
  this.currentEvent = null;
}
getUserName(userId: string): string {
  const user = this.allUsers.find(u => u.id === userId);
  return user ? user.name : 'Unknown User';
}
  onSubmit(): void {
    if (this.eventForm.invalid) return;

    const eventData: EventDTO = {
      ...this.eventForm.value,
      participantIds: this.selectedParticipants
    };

    this.eventService.createEvent(eventData)
      .subscribe({
        next: () => {
          this.loadEvents();
          this.closeModal();
        },
        error: (err) => {
          console.error('Error creating event:', err);
        }
      });
  }

  formatDate(dateString: string | Date): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  toggleParticipantSelection(userId: string): void {
    const index = this.selectedParticipants.indexOf(userId);
    if (index === -1) {
      this.selectedParticipants.push(userId);
    } else {
      this.selectedParticipants.splice(index, 1);
    }
  }

  isParticipantSelected(userId: string): boolean {
    return this.selectedParticipants.includes(userId);
  }
  compareById(item1: any, item2: any): boolean {
  return item1 && item2 ? item1 === item2 : item1 === item2;
}
 deleteEvent(eventId: string): void {
    if (confirm('Are you sure you want to delete this event?')) {
      this.eventService.deleteEvent(eventId)
        .subscribe({
          next: () => {
            this.loadEvents();
          },
          error: (err) => {
            console.error('Error deleting event:', err);
          }
        });
    }
  }
  openDetailsModal(event: Event): void {
    this.currentEvent = event;
    this.showDetailsModal = true;
  }
  onParticipantSelectionChange(event: MatSelectChange): void {
  this.selectedParticipantsForUpdate = event.value;
}
// Add this to your component class
isAdmin(): boolean {
  return this.currentUser?.roles === 'ADMINISTRATOR';
}
loadCurrentUser(): void {
  const userData = localStorage.getItem('user');
  if (userData) {
    try {
      this.currentUser = JSON.parse(userData);
      console.log('Current User:', this.currentUser); // Add this for debugging
    } catch (e) {
      console.error('Error parsing user data', e);
    }
  }
}

// Updated isLoggedIn to use currentUser
isLoggedIn(): boolean {
  return !!this.currentUser;
}
}