package com.lifejourney.engine2d;

import java.util.ArrayList;

public class CollidablePool {

    private static final String LOG_TAG = "CollidablePool";

    CollidablePool(Size regionSize, boolean response) {
        RectF region = new RectF(0, 0, (float)regionSize.width, (float)regionSize.height);
        objects = new ArrayList<>();
        quadTree = new QuadTree(0, region);
        this.response = response;
    }

    /**
     *
     * @param object
     */
    void add(CollidableObject object) {
        objects.add(object);
    }

    /**
     *
     * @param object
     */
    void remove(CollidableObject object) {
        objects.remove(object);
    }

    /**
     *
     */
    void checkCollision() {
        CollisionDetector collisionDetector = Engine2D.GetInstance().getCollisionDetector();

        for (CollidableObject object : objects) {
            quadTree.insert(object);
        }

        ArrayList<CollidableObject> candidatesList = new ArrayList<>();

        for (CollidableObject refObject : objects) {
            if (!refObject.isCollisionEnabled())
                continue;

            // Retreive candidates
            candidatesList.clear();
            candidatesList = quadTree.retrieve(candidatesList, refObject);

            // Collision check
            for (CollidableObject candidateObject : candidatesList) {
                if (refObject == candidateObject || candidateObject.isCollisionChecked() ||
                        !candidateObject.isCollisionEnabled()) {
                    continue;
                }

                collisionDetector.checkAndReponseCollision(refObject, candidateObject, response);
            }

            refObject.setCollisionChecked(true);
        }

        for (CollidableObject object : objects) {
            object.setCollisionChecked(false);
        }

        quadTree.clear();
    }

    /**
     *
     * @param egoObject
     * @param newPosition
     * @return
     */
    public boolean testCollision(CollidableObject egoObject, PointF newPosition) {
        CollisionDetector collisionDetector = Engine2D.GetInstance().getCollisionDetector();

        PointF oldPosition = egoObject.getPosition();
        egoObject.setPosition(newPosition);

        boolean result = false;
        for (CollidableObject candidateObject : objects) {
            if (egoObject == candidateObject || !candidateObject.isCollisionEnabled())
                continue;

            // Collision check
            if (collisionDetector.checkCollision(egoObject, candidateObject)) {
                result = true;
                break;
            }
        }

        egoObject.setPosition(oldPosition);
        return result;
    }

    private ArrayList<CollidableObject> objects;
    private QuadTree quadTree;
    private boolean response;
}
