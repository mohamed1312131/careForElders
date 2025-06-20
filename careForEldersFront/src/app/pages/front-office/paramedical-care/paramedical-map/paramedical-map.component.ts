import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from "@angular/forms";

// Définition d'interfaces pour les données (à adapter selon votre API)
interface Professional {
  id: string;
  name: string;
  specialty: string;
  contactInfo: string;
  address: string;
  distance?: number;
  // Ajoutez d'autres propriétés si nécessaire (ex: latitude, longitude)
}

interface Appointment {
  elderId: string;
  appointmentTime: Date;
  location: string;
  notes: string;
  professionalId: string;
}

@Component({
  selector: 'app-paramedical-map',
  templateUrl: './paramedical-map.component.html',
  styleUrls: ['./paramedical-map.component.scss']
})
export class ParamedicalMapComponent implements OnInit, OnDestroy {
  // Propriétés du formulaire de recherche
  public searchForm: FormGroup;
  public loading: boolean = false;
  public error: string | null = null;

  // Propriétés pour les professionnels et la sélection
  public filteredProfessionals: Professional[] = [];
  public selectedProfessional: Professional | null = null;

  // Propriétés pour le modal d'ajout de professionnel
  public showAddModal: boolean = false;
  public professionalForm: FormGroup;

  // Propriétés pour le modal de prise de rendez-vous
  public showAppointmentModal: boolean = false;
  public appointmentForm: FormGroup;

  constructor(private fb: FormBuilder) {
    // Initialisation du formulaire de recherche
    this.searchForm = this.fb.group({
      location: ['', Validators.required],
      specialty: [''],
      radius: [10, [Validators.required, Validators.min(1)]],
    });

    // Initialisation du formulaire d'ajout de professionnel
    this.professionalForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      specialty: ['', [Validators.required, Validators.minLength(2)]],
      contactInfo: ['', [Validators.required, Validators.minLength(5)]],
      address: ['', Validators.required],
    });

    // Initialisation du formulaire de prise de rendez-vous
    this.appointmentForm = this.fb.group({
      elderId: ['', Validators.required],
      appointmentTime: ['', Validators.required],
      location: [''],
      notes: [''],
    });
  }

  ngOnInit(): void {
    // Logique d'initialisation du composant
    console.log('ParamedicalMapComponent initialisé');
    // Vous pouvez charger des données initiales ici si nécessaire
  }

  ngOnDestroy(): void {
    // Logique de nettoyage (désabonnement, etc.)
    console.log('ParamedicalMapComponent détruit');
  }

  // Méthodes liées au formulaire de recherche
  public onSearch(): void {
    if (this.searchForm.valid) {
      this.loading = true;
      this.error = null;
      console.log('Recherche lancée avec :', this.searchForm.value);
      // Ici, vous feriez un appel à un service pour récupérer les professionnels
      // Exemple simulé :
      setTimeout(() => {
        this.filteredProfessionals = [
          { id: '1', name: 'Dr. Dupont', specialty: 'Physiothérapie', contactInfo: '0123456789', address: '123 Rue de la Paix', distance: 5 },
          { id: '2', name: 'Mme. Martin', specialty: 'Ergothérapie', contactInfo: '0987654321', address: '456 Avenue des Champs', distance: 12 },
        ];
        this.loading = false;
        if (this.filteredProfessionals.length === 0) {
          this.error = 'Aucun professionnel trouvé pour cette recherche.';
        }
      }, 1500);
    } else {
      console.log('Formulaire de recherche invalide');
      this.searchForm.markAllAsTouched(); // Afficher les erreurs de validation
    }
  }

  public getCurrentLocation(): void {
    this.loading = true;
    this.error = null;
    console.log('Récupération de la position actuelle...');
    // Ici, vous utiliseriez l'API de géolocalisation du navigateur
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          const lat = position.coords.latitude;
          const lng = position.coords.longitude;
          console.log(`Position actuelle : ${lat}, ${lng}`);
          // Vous pouvez utiliser un service de géocodage inverse pour obtenir l'adresse
          // Pour l'exemple, nous mettons à jour le champ de localisation directement
          this.searchForm.patchValue({ location: `Lat: ${lat}, Lng: ${lng}` });
          this.loading = false;
        },
        (err) => {
          console.error('Erreur de géolocalisation :', err);
          this.error = 'Impossible de récupérer votre position actuelle.';
          this.loading = false;
        }
      );
    } else {
      this.error = 'La géolocalisation n\'est pas supportée par votre navigateur.';
      this.loading = false;
    }
  }

  // Méthodes liées à la sélection de professionnel
  public selectProfessional(professional: Professional): void {
    this.selectedProfessional = professional;
    console.log('Professionnel sélectionné :', professional);
    // Ici, vous pourriez centrer la carte sur ce professionnel
  }

  // Méthodes liées au modal d'ajout de professionnel
  public openAddModal(): void {
    this.showAddModal = true;
    this.professionalForm.reset(); // Réinitialiser le formulaire à l'ouverture
    console.log('Ouverture du modal d\'ajout de professionnel');
  }

  public closeAddModal(): void {
    this.showAddModal = false;
    console.log('Fermeture du modal d\'ajout de professionnel');
  }

  public onAddProfessional(): void {
    if (this.professionalForm.valid) {
      console.log('Ajout du professionnel :', this.professionalForm.value);
      // Ici, vous feriez un appel à un service pour ajouter le professionnel
      // Après l'ajout réussi, vous pourriez fermer le modal et rafraîchir la liste
      this.closeAddModal();
      // Simuler un ajout et rafraîchir la liste
      const newProfessional: Professional = { ...this.professionalForm.value, id: `temp-${Date.now()}` };
      this.filteredProfessionals.push(newProfessional);
    } else {
      console.log('Formulaire d\'ajout de professionnel invalide');
      this.professionalForm.markAllAsTouched();
    }
  }

  // Méthodes liées au modal de prise de rendez-vous
  public openAppointmentModal(professional: Professional): void {
    this.selectedProfessional = professional; // S'assurer que le professionnel est sélectionné
    this.showAppointmentModal = true;
    this.appointmentForm.reset(); // Réinitialiser le formulaire
    // Pré-remplir la localisation si le professionnel a une adresse
    if (professional.address) {
      this.appointmentForm.patchValue({ location: professional.address });
    }
    console.log('Ouverture du modal de rendez-vous pour :', professional.name);
  }

  public closeAppointmentModal(): void {
    this.showAppointmentModal = false;
    this.selectedProfessional = null; // Réinitialiser le professionnel sélectionné
    console.log('Fermeture du modal de rendez-vous');
  }

  public onBookAppointment(): void {
    if (this.appointmentForm.valid && this.selectedProfessional) {
      const appointmentData: Appointment = {
        ...this.appointmentForm.value,
        professionalId: this.selectedProfessional.id,
      };
      console.log('Prise de rendez-vous :', appointmentData);
      // Ici, vous feriez un appel à un service pour enregistrer le rendez-vous
      // Après l'enregistrement réussi, vous pourriez fermer le modal
      this.closeAppointmentModal();
      alert(`Rendez-vous pris avec ${this.selectedProfessional.name} !`);
    } else {
      console.log('Formulaire de rendez-vous invalide ou professionnel non sélectionné');
      this.appointmentForm.markAllAsTouched();
    }
  }

  // Méthode pour supprimer un professionnel (si applicable)
  public deleteProfessional(professional: Professional): void {
    if (confirm(`Êtes-vous sûr de vouloir supprimer ${professional.name} ?`)) {
      console.log('Suppression du professionnel :', professional);
      // Ici, vous feriez un appel à un service pour supprimer le professionnel
      // Après la suppression, mettez à jour la liste
      this.filteredProfessionals = this.filteredProfessionals.filter(p => p.id !== professional.id);
      if (this.selectedProfessional?.id === professional.id) {
        this.selectedProfessional = null;
      }
    }
  }

  // Ajoutez ici d'autres méthodes ou propriétés nécessaires pour la carte elle-même
  // Par exemple, l'initialisation de la carte (Leaflet, Google Maps, etc.)
  // et la gestion des marqueurs.
}


