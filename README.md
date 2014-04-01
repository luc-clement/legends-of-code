Legends of Code 2
=================

[Legends of code 2](http://www.legendsofcode.fr/) is a 11 hours lang hackathon for students in last year of MSc organized by [D2SI](http://www.d2-si.fr/). It took place on 30 November 2013.

The goal of this challenge was to develop an artificial intelligence for a game. Once every hour during the day, our AI fought vs the AI of every other competitors on every maps.

I won this challenge with Alexis Terrat, and I would like to share here our strategy.

### The rules :

The map is an archipelago made of many islands. You can control an island if you have at least one ship on this island and for each island you control, you'll create a defined number of ships every turn on this island.

You can send fleets to take other islands. To do so, you define at the end of each turn objectives and assign them to fleets (that you create with the ships you own). Once an order is given, you can't change it (even if it takes more than one turn for your fleet to reach your target, what is almost always true). There is no fight on the see, just on islands. When two players meet on an island, there is a fight. the player with the least number of ships will lose (and all his ships will be destroyed), but the winner will lose as many ships as the looser.

Each turn, you have information about : The position of all the fleets (and their size), the given orders (even those of the enemy), the position and owner of all islands.

There are always two players, and there may be a neutral player, who own islands and create ships, doesn't attack anyone, but defends his islands.

The game end when a player doesn't have ships anymore, or after 800 turns.
