import { NavItem } from './nav-item/nav-item';

export const navItems: NavItem[] = [
  {
    navCap: 'Home',
  },
  {
    displayName: 'Dashboard',
    iconName: 'layout-dashboard',
    route: '/dashboard',
  },
  {
    navCap: 'Administration', // Administration Section
  },
  {
    displayName: 'Users Administration',
    iconName: 'settings', // Choose an appropriate icon
    route: '/user/users',},
  {
    navCap: 'Medical Record', // Administration Section
  },
  {
    displayName: 'medical Record',
    iconName: 'lock',
    route: '/user/medicalRecord',
  },
  {
    navCap: 'Nutrition',
  },
  {
    displayName: 'Nutrition Admin Dashboard',
    iconName: 'aperture',
    route: '/user/userProfile/nutrition/admin-dashboard',
  },
  {
    displayName: 'Nutrition Plans List',
    iconName: 'aperture',
    route: '/user/userProfile/nutrition/list',
  },
  {
    displayName: 'Nutrition Plan Generator',
    iconName: 'aperture',
    route: '/user/userProfile/nutrition/plan-generator',
  },
  {
    navCap: 'Paramedical Care',
  },
  {
    displayName: 'Paramedical Care',
    iconName: 'user-heart',
    route: '/user/userProfile/paramedical-care',
    children: [
      {
        displayName: 'Dashboard',
        iconName: 'layout-dashboard',
        route: '/user/userProfile/paramedical-care',
      },
      {
        displayName: 'Professional List',
        iconName: 'users',
        route: '/user/userProfile/paramedical-care/professional-list',
      },
      {
        displayName: 'Appointments',
        iconName: 'calendar-event',
        route: '/user/userProfile/paramedical-care/appointment',
      },
      {
        displayName: 'Find Nearby Professionals',
        iconName: 'map-pin',
        route: '/user/userProfile/paramedical-care/nearby-professionals',
      },
      {
        displayName: 'Paramedical Map',
        iconName: 'map',
        route: '/user/userProfile/paramedical-care/paramedical-map',
      }
    ]
  },

  {
    navCap: 'Ui Components',
  },
  {
    displayName: 'Badge',
    iconName: 'rosette',
    route: 'admin/ui-components/badge',
  },
  {
    displayName: 'Reservation',
    iconName: 'rosette',
    route: '/admin/ui-components/Reservation',
  },
  {
    displayName: 'Abonnement',
    iconName: 'rosette',
    route: '/admin/ui-components/Abonnement',
  },
  {
    displayName: 'Chips',
    iconName: 'poker-chip',
    route: '/ui-components/chips',
  },
  {
    displayName: 'Lists',
    iconName: 'list',
    route: '/ui-components/lists',
  },
  {
    displayName: 'Menu',
    iconName: 'layout-navbar-expand',
    route: 'admin/ui-components/menu',
  },
  {
    displayName: 'Tooltips',
    iconName: 'tooltip',
    route: '/ui-components/tooltips',
  },
  {
    navCap: 'Auth',
  },
  {
    displayName: 'Login',
    iconName: 'lock',
    route: '/authentication/login',
  },
  {
    displayName: 'Register',
    iconName: 'user-plus',
    route: '/authentication/register',
  },
  {
    navCap: 'Extra',
  },
  {
    displayName: 'Icons',
    iconName: 'mood-smile',
    route: '/extra/icons',
  },
  {
    displayName: 'Sample Page',
    iconName: 'aperture',
    route: '/extra/sample-page',
  },
];
