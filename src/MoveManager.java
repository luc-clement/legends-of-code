package fr.d2si.loc.bot;

import java.util.ArrayList;
import java.util.List;

import com.d2si.loc.api.datas.Coordinate;
import com.d2si.loc.api.datas.Island;

public class MoveManager {
	
	//calcule la distance entre 2 points de la map.
	public static double getDistance(Coordinate coord1, Coordinate coord2){
		double distance;
		int x1, x2, y1, y2;
		x1 = coord1.getX();
		y1 = coord1.getY();
		x2 = coord2.getX();
		y2 = coord2.getY();
		distance = (double) (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1);
		distance = Math.sqrt(distance);
		return distance;
	}
	
	//calcule le nombre de tour necessaire pour rejoindre 2 points de la map.
	public static int getDistanceInTurn(Coordinate coord1, Coordinate coord2){
		double nTurns = getDistance(coord1, coord2)/5.;
		return (int) Math.ceil(nTurns);
	}
	
	//trouve l'ile de 'destIslands' la plus proche de 'island' 
	//en excluant 'island' si elle appartient a la liste 'destIslands'
	//renvoie null si 'destIslands' est vide
	public static Island getNearestIsland(Island island, List<Island> destIslands){
		Island nearestIsland = null;
		double currDistance;
		double minDistance = Double.MAX_VALUE;
		for(Island aIsland : destIslands){
			currDistance = getDistance(island.getPosition(), aIsland.getPosition());
			if(currDistance > 0 && currDistance < minDistance){
				minDistance = currDistance;
				nearestIsland = aIsland;
			}
		}
		return nearestIsland;
	}
	//trouve toutes les iles de 'islands' dans une boule fermee de centre 'island' et de rayon 'distanceInTurn'
	//renvoie une liste vide si aucune ile n'a ete trouvee.
	public static List<Island> getNearIslands(Island island, List<Island> islands, int distanceInTurn){
		List<Island> nearIslands = new ArrayList<Island>();
		double currDistance;
		for(Island aIsland : islands){
			currDistance = getDistanceInTurn(island.getPosition(), aIsland.getPosition());
			if(currDistance > 0 && currDistance <= distanceInTurn){
				nearIslands.add(aIsland);
			}
		}
		return nearIslands;
	}
	
	//trouve toutes les iles atteignables en 'distanceInTurn' pour les flottes partant de 'island'
	//retourne une liste vide si aucune ile n'a ete trouvee.
	public static List<Island> getIslandsOnCircle(Island island, List<Island> islands, int distanceInTurn){
		List<Island> nearIslands = new ArrayList<Island>();
		double currDistance;
		for(Island aIsland : islands){
			currDistance = getDistanceInTurn(island.getPosition(), aIsland.getPosition());
			if(currDistance == distanceInTurn){
				nearIslands.add(aIsland);
			}
		}
		return nearIslands;
	}
}