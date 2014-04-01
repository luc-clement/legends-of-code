package fr.d2si.loc.bot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.d2si.loc.api.datas.Board;
import com.d2si.loc.api.datas.Fleet;
import com.d2si.loc.api.datas.Island;

public class ShipsManager {
	
	public static int predictionSize = 10;
	
	//prediction du nombre de bateaux sur nos iles
	public static HashMap<Island, Integer[]> shipsCountPredic;
	
	//prediction du nombre de bateaux sur les iles neutres
	public static HashMap<Island, Integer[]> neutralShipsCountPredic;
	
	//etudie sur un nombre de tour de 'predictionSize' le nombre d'unites 
	//sur chaque ile neutre ou nous appartenant si aucune action n'est faite durant notre tour
	//il faut donc appeler calcPredic() au debut de chaque tour et ensuite actualiser shipsCountPredic
	//et neutralShipsPredic en fonction de nos mouvements
	public static void calcPredic(Board board){
		int i, currShipCount;
		List<Island> myIslands = board.getMineIslands();
		List<Island> neutralIslands = board.getNeutralIslands();
		shipsCountPredic = new HashMap<Island, Integer[]>(myIslands.size());
		
		//ajout du shipCount courant et de la production a la prediction de nos iles
		for(Island island : myIslands){
			Integer[] prediction = new Integer[predictionSize];
			currShipCount = island.getShipCount();
			for(i = 0; i < predictionSize; i++){
				prediction[i] = currShipCount + i*island.getGrowthRate();
			}
			shipsCountPredic.put(island, prediction);
		}
		
		//ajout du shipCount courant a la prediction des iles neutres
		neutralShipsCountPredic = new HashMap<Island, Integer[]>(myIslands.size());
		for(Island island : neutralIslands){
			Integer[] prediction = new Integer[predictionSize];
			currShipCount = island.getShipCount();
			for(i = 0; i < predictionSize; i++){
				prediction[i] = currShipCount;
			}
			neutralShipsCountPredic.put(island, prediction);
		}
		
		//prise en compte de nos renforts dans la prediction pour nos iles
		Island destination;
		int remainingTurns;
		for(Fleet fleet : board.getMineFleets()){
			destination = fleet.getDestination();
			remainingTurns = MoveManager.getDistanceInTurn(fleet.getPosition(), destination.getPosition());
			if(myIslands.contains(destination) && remainingTurns < predictionSize){
				for(i = remainingTurns; i < predictionSize; i++)
					shipsCountPredic.get(destination)[i] += fleet.getShipCount();
			}
		}
		
		//prise en compte des attaques ennemies pour les 2 predictions
		for(Fleet fleet : board.getOpponentsFleets()){
			destination = fleet.getDestination();
			remainingTurns = MoveManager.getDistanceInTurn(fleet.getPosition(), destination.getPosition());
			if(myIslands.contains(destination) && remainingTurns < predictionSize){
				for(i = remainingTurns; i < predictionSize; i++)
					shipsCountPredic.get(destination)[i] -= fleet.getShipCount();
			} else if(neutralIslands.contains(destination) && remainingTurns < predictionSize){
				for(i = remainingTurns; i < predictionSize; i++)
					neutralShipsCountPredic.get(destination)[i] -= fleet.getShipCount();
			}
		}
	}
	
	//retourne la liste des iles de 'islands' qui sont pleines ou qui le seront au prochain tour
	public static List<Island> getFullIslands(List<Island> islands){
		List<Island> fullIslands = new ArrayList<Island>();
		for(Island island : islands){
			if(island.getRemainingShipToCreate() <= island.getGrowthRate()){
				fullIslands.add(island);
			}
		}
		return fullIslands;
	}
}