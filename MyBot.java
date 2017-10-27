import hlt.*;

import java.util.ArrayList;

public class MyBot {

    public static void main(final String[] args) {
        final Networking networking = new Networking();
        final GameMap gameMap = networking.initialize("TobySuch");

        final ArrayList<Move> moveList = new ArrayList<>();
        final ArrayList<Planet> unownedPlanets = new ArrayList<>();
        final ArrayList<Planet> ownedPlanets = new ArrayList<>();
        final ArrayList<Planet> planetHasFreeDocks = new ArrayList<>();

        for (;;) {
            moveList.clear();
            unownedPlanets.clear();
            ownedPlanets.clear();
            planetHasFreeDocks.clear();
            gameMap.updateMap(Networking.readLineIntoMetadata());

            for (Planet planet : gameMap.getAllPlanets().values()) {
                if (planet.isOwned()) {
                    ownedPlanets.add(planet);
                    if (planet.getOwner() == gameMap.getMyPlayer().getId() & !planet.isFull()) {
                        planetHasFreeDocks.add(planet);
                    }
                } else {
                    unownedPlanets.add(planet);
                }
            }

            for (final Ship ship : gameMap.getMyPlayer().getShips().values()) {
                if (ship.getDockingStatus() != Ship.DockingStatus.Undocked) {
                    continue;
                }

                // Look for unowned planet to claim
                Entity target = null;
                double closestDistance = -1;
                for (Planet planet : unownedPlanets) {
                    double distance = ship.getDistance(planet);
                    if (closestDistance == -1 | distance < closestDistance) {
                        target = planet;
                        closestDistance = distance;
                    }
                }

                // Looks for own planets which have free docking spots
                if (target == null) {
                    closestDistance = -1;
                    for (Planet planet : planetHasFreeDocks) {
                        double distance = ship.getDistance(planet);
                        if (closestDistance == -1 | distance < closestDistance) {
                            target = planet;
                            closestDistance = distance;
                        }
                    }
                }

                // Look for enemy planet to take over
                if (target == null) {
                    closestDistance = -1;
                    Planet closestPlanet = null;
                    for (Planet planet : ownedPlanets) {
                        if (planet.getOwner() != gameMap.getMyPlayer().getId()) {
                            double distance = ship.getDistance(planet);
                            if (closestDistance == -1 | distance < closestDistance) {
                                closestPlanet = planet;
                                closestDistance = distance;
                            }
                        }
                    }
                    if (closestPlanet != null) {
                        target = gameMap.getShip(closestPlanet.getOwner(), closestPlanet.getDockedShips().get(0));
                    }
                }

                if (target != null) {
                    if (target instanceof Planet) {
                        Planet targetPlanet = (Planet) target;
                        if (ship.canDock(targetPlanet)) {
                            moveList.add(new DockMove(ship, targetPlanet));
                            continue;
                        }
                    }

                    final ThrustMove newThrustMove = Navigation.navigateShipToDock(gameMap, ship, target, Constants.MAX_SPEED);
                    if (newThrustMove != null) {
                        moveList.add(newThrustMove);
                    }
                }
            }
            Networking.sendMoves(moveList);
        }
    }
}
