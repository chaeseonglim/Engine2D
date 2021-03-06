package com.lifejourney.engine2d;

import android.graphics.Matrix;

import java.util.ArrayList;
import java.util.Arrays;

public class Shape {

    private static final String LOG_TAG = "Shape";

    public Shape() {
    }

    // circle
    public Shape(float radius) {
        this.radius = radius;
    }

    // polygon
    public Shape(ArrayList<PointF> vertices) {
        originalVertices = vertices;
        radius = calcMinimumRadius();
    }

    // polygon
    public Shape(PointF[] vertices) {
        originalVertices = new ArrayList<>(Arrays.asList(vertices));
        radius = calcMinimumRadius();
    }

    public Shape(Shape shape) {
        originalVertices = new ArrayList<>(shape.originalVertices);
        radius = shape.radius;
        position = new PointF(shape.position);
        rotation = shape.rotation;
    }

    /**
     *
     * @param m
     * @return
     */
    public Shape multiply(float m) {
        if (originalVertices != null) {
            for (PointF vertex : originalVertices) {
                vertex.multiply(m);
            }
            cached = false;
            radius = calcMinimumRadius();
        }
        else {
            radius *= m;
        }

        return this;
    }

    /**
     *
     * @param p
     * @return
     */
    public Shape add(PointF p) {
        if (originalVertices.size() > 0) {
            for (PointF vertex : originalVertices) {
                vertex.add(p);
            }
            radius = calcMinimumRadius();
            cached = false;
        }

        return this;
    }

    /**
     *
     * @param p
     * @return
     */
    public Shape subtract(PointF p) {
        if (originalVertices.size() > 0) {
            for (PointF vertex : originalVertices) {
                vertex.subtract(p);
            }
            radius = calcMinimumRadius();
            cached = false;
        }

        return this;
    }

    /**
     *
     * @return
     */
    public boolean isValid() {
        return originalVertices != null || radius != 0.0f;
    }

    /**
     *
     * @return
     */
    public boolean isCircle() {
        return originalVertices == null && radius != 0.0f;
    }

    /**
     *
     * @return
     */
    private float calcMinimumRadius() {
        float radius = 0.0f;
        for (PointF vertex: originalVertices) {
            radius = Math.max(vertex.vectorize().length(), radius);
        }
        return radius;
    }

    /**
     *
     * @return
     */
    public float getRadius() {
        return radius;
    }

    /**
     *
     * @return
     */
    public PointF getPosition() {
        return position;
    }

    /**
     *
     * @param position
     */
    public void setPosition(PointF position) {
        this.position = position;
    }

    /**
     *
     * @return
     */
    public float getRotation() {
        return rotation;
    }

    /**
     *
     * @param rotation
     */
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    /**
     *
     * @return
     */
    public ArrayList<PointF> getVertices() {
        updateCache();
        return cachedVertices;
    }

    /**
     *
     */
    private void updateCache() {
        if (cached && cachedPosition.equals(position) && rotation == cachedRotation) {
            return;
        }

        Matrix transform = new Matrix();
        transform.postRotate(rotation);
        transform.postTranslate(position.x, position.y);

        float[] vertices = new float[originalVertices.size()*2];
        for (int i = 0; i < originalVertices.size(); ++i) {
            vertices[i*2] = originalVertices.get(i).x;
            vertices[i*2+1] = originalVertices.get(i).y;
        }
        transform.mapPoints(vertices);
        cachedVertices = new ArrayList<>();
        for (int i = 0; i < originalVertices.size(); ++i) {
            cachedVertices.add(new PointF(vertices[i*2], vertices[i*2+1]));
        }
        cachedPosition = new PointF(position);
        cachedRotation = rotation;
        cached = true;
    }

    /**
     *
     * @return
     */
    ArrayList<Vector2D> getAxes() {
        ArrayList<Vector2D> axes = new ArrayList<>();

        ArrayList<PointF> vertices = getVertices();
        for (int i = 0; i < vertices.size(); ++i) {
            Vector2D p1 = vertices.get(i).vectorize();
            Vector2D p2 =
                    vertices.get((i+1==vertices.size())?0:i+1).vectorize();
            Vector2D edge = p1.subtract(p2);
            Vector2D normal = edge.perpendicular().normalize();
            axes.add(normal);
        }

        return axes;
    }

    /**
     *
     * @param direction
     * @return
     */
    Vector2D getSupportPoint(Vector2D direction) {
        if (isCircle()) {
            return direction.clone().normalize().multiply(radius).add(position.vectorize());
        }
        else {
            float bestProjection = -Float.MAX_VALUE;
            Vector2D bestVertex = null;

            ArrayList<PointF> vertices = getVertices();
            for (int i = 0; i < vertices.size(); ++i) {
                Vector2D v = vertices.get(i).vectorize();
                float projection = v.dot(direction);
                if (projection > bestProjection) {
                    bestProjection = projection;
                    bestVertex = v;
                }
            }

            return bestVertex;
        }
    }

    /**
     *
     * @return
     */
    RectF getMinimumCoveredRect() {
        if (isCircle()) {
            return new RectF(position.x - radius, position.y - radius,
                    position.x + radius, position.y + radius);
        }
        else {
            float minLeft = Float.MAX_VALUE, minTop = Float.MAX_VALUE,
                    maxRight = Float.MIN_VALUE, maxBottom = Float.MIN_VALUE;
            for (PointF vertex: getVertices()) {
                minLeft = Math.min(minLeft, vertex.x);
                maxRight = Math.max(maxRight, vertex.x);
                minTop = Math.min(minTop, vertex.y);
                maxBottom = Math.max(maxBottom, vertex.y);
            }

            return new RectF(minLeft, minTop, maxRight - minLeft, maxBottom - minTop);
        }
    }

    private ArrayList<PointF> originalVertices;
    private float radius = 0.0f;
    private PointF position = new PointF();
    private float rotation = 0.0f;

    // Cached information
    private boolean cached = false;
    private ArrayList<PointF> cachedVertices;
    private PointF cachedPosition;
    private float cachedRotation;
}
