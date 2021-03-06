import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { UserComponent } from './component/user/user.component';
import { AdminComponent } from './component/admin/admin.component';
import { LoginComponent } from './component/login/login.component';
import { UserDecksComponent } from './component/user-decks/user-decks.component';
import { UserCardsComponent } from './component/user-cards/user-cards.component';
import { CardDetailComponent } from './component/card-detail/card-detail.component';
import { DeckDetailComponent } from './component/deck-detail/deck-detail.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: 'user', component: UserComponent, children: [
      {
        path: 'decks',
        component: UserDecksComponent,
      },
      {
        path: 'cards',
        component: UserCardsComponent,
      }, {
        path: 'decks/:did',
        component: DeckDetailComponent,
      },
      {
        path: '**',
        redirectTo: 'cards',
        pathMatch: 'full'
      },
    ]
  },
  {
    path: 'cards/detail',
    component: CardDetailComponent
  },
  { path: 'admin', component: AdminComponent },
  { path: '**', redirectTo: '/login', pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppRoutingModule { }
