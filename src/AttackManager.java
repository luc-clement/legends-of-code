package fr.d2si.loc.bot;

import java.util.ArrayList;
import java.util.List;

import com.d2si.loc.api.datas.Board;
import com.d2si.loc.api.datas.Fleet;
import com.d2si.loc.api.datas.Island;

public class AttackManager {
	
	// Dans cette attaque, on repère les îles neutres qui vont être capturées par l'ennemi,
	// et on lance nos troupes de façon à arriver juste après la capture, 
	// sur une ville très faible ennemie, et sans avoir dépensé le moindre bateau pour
	// se battre contre des bateaux neutres, on les garde pour l'adversaire.
	public static List<Fleet> attackPostCapture(Board board) {
		
		List<Fleet> result = new ArrayList<>();
		int i=0;
		List<Island> neutralIslands = board.getNeutralIslands();
		List<Island> myIslands = board.getMineIslands();
		
		
		for (Island neutralIsland : neutralIslands) {
			Integer shipsCount[] = ShipsManager.neutralShipsCountPredic.get(neutralIsland);
			int conquestRound = -1;
			
			// On repère le round auquel l'île va être capturée
			for (i=ShipsManager.predictionSize-1; i>=0; --i) {
				if (shipsCount[i] <= 0)
					conquestRound = i;
			}
			
			// On envoie des troupes des îles qui arriveront juste après la capture si il en existe
			List<Island> counterIslands = MoveManager.getIslandsOnCircle(neutralIsland, myIslands, i+1);
			if (conquestRound > 0 && counterIslands.size() != 0) {
				
				for (Island counterIsland : counterIslands) {
					// On calcule les troupes disponibles, de façon à ne pas se mettre en danger pour autant
					int disponibilite = Integer.MAX_VALUE;
					int disponibiliteTemp;
					for (i=0; i<ShipsManager.predictionSize; ++i) {
						disponibiliteTemp = ShipsManager.shipsCountPredic.get(counterIsland)[i] - 1;
						if (disponibiliteTemp < disponibilite)
							disponibilite = disponibiliteTemp;
					}
					
					// Si disponibilite > 0, on envoie les troupes
					if (disponibilite > 0) {
						Fleet fleet = new Fleet(neutralIsland, counterIsland, disponibilite);
						result.add(fleet);
						// On actualise le shipsCountPredic
						for (i=0; i<ShipsManager.predictionSize; ++i) {
							ShipsManager.shipsCountPredic.get(counterIsland)[i] -= disponibilite;
						}
					}
				}
				
			}
		}
		return result;
	}
	
	
	// Les îles qui risquent de se retrouver complètes dans leurs ports envoient des attaques 
	// pour libérer de la place, vers l'ennemi le plus faible de la carte.
	public static List<Fleet> attackSurplus(Board board) {
		List<Fleet> result = new ArrayList<>();
		int i=0;
		List<Island> myIslands = board.getMineIslands();
		List<Island> ennemyIslands = board.getOpponentsIslands();
		
		if (ennemyIslands.size() != 0) {
			// On détermine l'ennemi le plus faible, s'il y a encore des ennemis 
			// (attention au nullPointerException si l'ennemi n'a plus d'île mais encore
			// des flottes sur l'eau).
			Island weakestIsland = ennemyIslands.get(0);
			int defense = weakestIsland.getShipCount();
			for (Island ennemyIsland : ennemyIslands) {
				if (defense > ennemyIsland.getShipCount()) {
					weakestIsland = ennemyIsland;
					defense = ennemyIsland.getShipCount();
				}
			}
			
			// On attaque avec les villes remplies
			for (Island currentIsland : ShipsManager.getFullIslands(myIslands)) {
				int shipToSend = Integer.MAX_VALUE;
				for (i=0; i<ShipsManager.predictionSize; ++i) {
					if (ShipsManager.shipsCountPredic.get(currentIsland)[i] - 1 < shipToSend)
						shipToSend = ShipsManager.shipsCountPredic.get(currentIsland)[i] - 1;
				}
				
				if (shipToSend > 0) {
					Fleet fleet = new Fleet(weakestIsland, currentIsland, shipToSend);
					result.add(fleet);
					for (i=0; i<ShipsManager.predictionSize; ++i) {
						ShipsManager.shipsCountPredic.get(currentIsland)[i] -= shipToSend;
					}
					
				}			
			}
		}
		return result;
	}
	
	// Les îles qui ne sont pas en danger attaquent avec quelques bateaux (de façon
	// tout de même à ne pas se mettre en danger) l'ennemi le plus proche
	public static List<Fleet> attackFinale(Board board) {
		List<Fleet> result = new ArrayList<>();
		int i=0;
		List<Island> myIslands = board.getMineIslands();
		List<Island> ennemyIslands = board.getOpponentsIslands();
				
		for (Island currentIsland : myIslands) {
			// On vérifie qu'on ne se met pas en danger en attaquant
			int shipToSend = Integer.MAX_VALUE;
			for (i=0; i<ShipsManager.predictionSize; ++i) {
				if (ShipsManager.shipsCountPredic.get(currentIsland)[i] - 1 < shipToSend)
					shipToSend = ShipsManager.shipsCountPredic.get(currentIsland)[i] - 1;
			}
			
			Island nearestEnnemy = MoveManager.getNearestIsland(currentIsland, ennemyIslands);
			
			// Si ce n'est pas le cas et qu'il existe encore une île ennemie, 
			// on attaque la plus proche
			if (shipToSend > 0 && nearestEnnemy != null && board.getTurnNumber() > 8) {
				Fleet fleet = new Fleet(nearestEnnemy, currentIsland, shipToSend);
				result.add(fleet);
			}
		}
		
		return result;
	}

}
