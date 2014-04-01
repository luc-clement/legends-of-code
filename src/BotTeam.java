package fr.d2si.loc.bot;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.d2si.loc.api.Bot;
import com.d2si.loc.api.datas.*;

public class BotTeam implements Bot {

	private static final Logger log = LogManager.getLogger(BotTeam.class);

	@Override
	public void init(Board board) {
		// Special initializations (if needed)
		log.info("init: " + board);
	}

	@Override
	public void onFleetError(int turnNumber, String messageError, Fleet fleetInError) {
		// A previously fleet sent was illegal...
		log.info("onFleetError: turnNumber[" + turnNumber + "] msg[" + messageError + "] fleet:" + fleetInError);
	}

	@Override
	public void onGameEnded(boolean areYouWinner, Board board) {
		// Am I winner?
		log.info("onGameEnded: winner[" + areYouWinner + "]");
	}

	@Override
	public List<Fleet> onNewTurn(Board board) {
		// Decide what fleets to send on this new turn
		List<Fleet> fleetsToSendOnThisTurn = new ArrayList<>();

		// Initialisation des prédictions 
		ShipsManager.calcPredic(board);
		
		// Phase "attaque post-capture"
		fleetsToSendOnThisTurn.addAll(AttackManager.attackPostCapture(board));
		
		
		// Phase défensive
		fleetsToSendOnThisTurn.addAll(DefenseManager.defense(board));
		
		// Phase offensive - Fin du tour
		// Les îles en risque de surplus attaquent l'ennemi le plus faible pour vider un peu leurs ports
		fleetsToSendOnThisTurn.addAll(AttackManager.attackSurplus(board));		
		// Les îles qui ne risquent pas de se faire conquérir dans les 
		// 'predictSize' prochains tours peuvent attaquer, ils attaquent l'ennemi le plus proche
		fleetsToSendOnThisTurn.addAll(AttackManager.attackFinale(board));	
		
		

		// Return all new fleets
		return fleetsToSendOnThisTurn;
	}

}