package com.lifejourney.engine2d;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

public abstract class PathFinder {

    private static final String LOG_TAG = "PathFinder";

    public PathFinder() {
    }

    public PathFinder(Point start, Point target) {

        set(start, target);
    }

    public void set(Point start, Point target) {

        this.start = start;
        this.target = target;
    }

    /**
     * Finding path using A* algorithm
     */
    public ArrayList<Waypoint> findOptimalPath() {

        PriorityQueue<Waypoint> openList = new PriorityQueue<>();
        ArrayList<Waypoint> closeList = new ArrayList<>();

        // Add start point to open list
        openList.offer(new Waypoint(start, null,0.0f));

        // Search through open list
        while (openList.size() > 0) {
            Waypoint waypoint = openList.poll();

            // Check if it's goal
            assert waypoint != null;
            if (waypoint.getPosition().equals(target)) {
                // Get optimal path
                optimalPath = new ArrayList<>();
                while (waypoint != null) {
                    optimalPath.add(waypoint);
                    waypoint = waypoint.getParent();
                }
                Collections.reverse(optimalPath);
                return optimalPath;
            }

            if (closeList.contains(waypoint)) {
                continue;
            }

            // Add waypoint to close list
            closeList.add(waypoint);

            // Find possible waypoints from here
            ArrayList<Waypoint> possibleWaypoints = getPossibleWaypoints(waypoint);

            for (Waypoint possibleWaypoint: possibleWaypoints) {
                // Calculate heuristic score
                possibleWaypoint.setCostToTarget(calculateCostToTarget(possibleWaypoint, target));

                // Check if this candidate is in open list already
                boolean skipThisWaypoint = false;
                if (openList.contains(possibleWaypoint)) {
                    for (Waypoint w : openList) {
                        if (w.equals(possibleWaypoint) && w.getCost() <= possibleWaypoint.getCost()) {
                            skipThisWaypoint = true;
                            break;
                        }
                    }
                    if (skipThisWaypoint) {
                        continue;
                    }
                }

                // Check if it's in close list
                if (closeList.contains(possibleWaypoint)) {
                    Waypoint w = closeList.get(closeList.indexOf(possibleWaypoint));
                    if (w.getCost() <= possibleWaypoint.getCost()) {
                        continue;
                    }

                    closeList.remove(possibleWaypoint);
                }

                // Replace one if the exist one in open list has worse score than candidate
                openList.remove(possibleWaypoint);
                openList.offer(possibleWaypoint);
            }
        }

        Log.e(LOG_TAG, "Failed to find a path!!!");
        return null;
    }

    /**
     *
     * @param waypoint
     * @return
     */
    private ArrayList<Waypoint> getPossibleWaypoints(Waypoint waypoint) {

        ArrayList<Waypoint> neighbors = getNeighborWaypoints(waypoint);
        ArrayList<Waypoint> possibleWaypoints = new ArrayList<>();

        for (Waypoint neighbor : neighbors) {
            if (isMovable(waypoint.getPosition(), neighbor.getPosition())) {
                assert waypoint.getCostFromStart() == waypoint.getCostFromStart() + 1;
                possibleWaypoints.add(neighbor);
            }
        }
        return possibleWaypoints;
    }

    /**
     *
     * @return
     */
    public ArrayList<Waypoint> getOptimalPath() {
        return optimalPath;
    }

    /**
     *
     * @param waypoint
     * @return
     */
    protected abstract ArrayList<Waypoint> getNeighborWaypoints(Waypoint waypoint);

    /**
     *
     * @param current
     * @param target
     * @return
     */
    protected abstract float calculateCostToTarget(Waypoint current, Point target);

    /**
     *
     * @param current
     * @param target
     * @return
     */
    protected abstract boolean isMovable(Point current, Point target);

    private Point start;
    private Point target;
    private ArrayList<Waypoint> optimalPath;
}
