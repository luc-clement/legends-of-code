package fr.d2si.loc.bot;

import java.util.ArrayList;
import java.util.List;

import com.d2si.loc.api.datas.Board;
import com.d2si.loc.api.datas.Fleet;
import com.d2si.loc.api.datas.Island;

public class DefenseManager {

	public static List<Fleet> defense(Board board) {
		List<Fleet> result = new ArrayList<>();
		int i=0;
		List<Island> myIslands = board.getMineIslands();
		
		for (Island currentIsland : myIslands) {
			Integer shipsCount[] = ShipsManager.shipsCountPredic.get(currentIsland);
			int danger = -1;
			int nbAide = 0;
			
			// On vérifie si l'île est en danger
			// Si c'est le cas, danger vaut le tour le plus proche où elle sera conquise par l'ennemi
			for (i=ShipsManager.predictionSize-1; i>0; --i) {
				if (shipsCount[i] <= 0) {
					danger = i;
					nbAide = 1 - shipsCount[i];
				}
			}
			
			// On envoie des renforts des îles qui sont assez proches et qui ont des troupes dispos
			for (Island helpingIsland : MoveManager.getNearIslands(currentIsland, myIslands, danger) ) {
				if (nbAide > 0) {
					// Nombre de bateaux qu'on pourra envoyer en renfort
					int disponibilite = Integer.MAX_VALUE;
					int disponibiliteTemp;
					// disponibilite = Le minimum de ShipsCountPredic
					for (i=0; i<ShipsManager.predictionSize; ++i) {
						disponibiliteTemp = ShipsManager.shipsCountPredic.get(helpingIsland)[i];
						if (disponibiliteTemp < disponibilite)
							disponibilite = disponibiliteTemp;
					}
					
					// On envoie le minimum entre ce dont l'île à besoin comme renforts
					// et ce qu'on peut se permettre d'envoyer :
					int shipToSend = Math.min(disponibilite, nbAide);
					Fleet fleet = new Fleet(currentIsland, helpingIsland, shipToSend);
					result.add(fleet);
					
					// On actualise la Hashmap
					for (i=0; i<ShipsManager.predictionSize; ++i) {
						ShipsManager.shipsCountPredic.get(currentIsland)[i] += shipToSend;
						ShipsManager.shipsCountPredic.get(helpingIsland)[i] -= shipToSend;
						nbAide -= shipToSend; 
					}
				}
			}
		}
		
		return result;
	}
	
	
}
