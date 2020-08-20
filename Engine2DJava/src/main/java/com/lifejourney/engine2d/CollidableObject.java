package com.lifejourney.engine2d;

import android.graphics.Color;

import java.util.ArrayList;

public class CollidableObject extends Object {

    static final String LOG_TAG = "CollidableObject";

    @SuppressWarnings("unchecked")
    public static class Builder<T extends CollidableObject.Builder<T>> extends Object.Builder<T> {

        // optional parameter
        protected Shape shape = new Shape();
        protected Vector2D force = new Vector2D();
        protected float torque = 0.0f;
        protected float maxForce = Float.MAX_VALUE;
        protected float maxTorque = Float.MAX_VALUE;
        protected float mass = 1.0f;
        protected float inertia = 1.0f;
        protected float friction = 0.0f;
        protected float restitution = 0.5f;
        protected Vector2D velocity = new Vector2D();
        protected float angularVelocity = 0.0f;
        protected float maxVelocity = Float.MAX_VALUE;
        protected float maxAngularVelocity = Float.MAX_VALUE;

        public Builder(PointF position) {
            super(position);
        }
        public T velocity(Vector2D velocity) {
            this.velocity = velocity;
            return (T)this;
        }
        public T angularVelocity(float angularVelocity) {
            this.angularVelocity = angularVelocity;
            return (T)this;
        }
        public T maxVelocity(float maxVelocity) {
            this.maxVelocity = maxVelocity;
            return (T)this;
        }
        public T maxAngularVelocity(float maxAngularVelocity) {
            this.maxAngularVelocity = maxAngularVelocity;
            return (T)this;
        }
        public T shape(Shape shape) {
            this.shape = shape;
            return (T)this;
        }
        public T force(Vector2D force) {
            this.force = force;
            return (T)this;
        }
        public T torque(float torque) {
            this.torque = torque;
            return (T)this;
        }
        public T maxForce(float maxForce) {
            this.maxForce = maxForce;
            return (T)this;
        }
        public T maxTorque(float maxTorque) {
            this.maxTorque = maxTorque;
            return (T)this;
        }
        public T mass(float mass) {
            this.mass = mass;
            return (T)this;
        }
        public T inertia(float inertia) {
            this.inertia = inertia;
            return (T)this;
        }
        public T friction(float friction) {
            this.friction = friction;
            return (T)this;
        }
        public T restitution(float restitution) {
            this.restitution = restitution;
            return (T)this;
        }
        public CollidableObject build() {
            return new CollidableObject(this);
        }
    }

    protected CollidableObject(Builder builder) {

        super(builder);

        velocity = builder.velocity;
        angularVelocity = builder.angularVelocity;
        maxVelocity = builder.maxVelocity;
        maxAngularVelocity = builder.maxAngularVelocity;

        shape = builder.shape;
        force = builder.force;
        torque = builder.torque;
        maxForce = builder.maxForce;
        maxTorque = builder.maxTorque;
        mass = builder.mass;
        invMass = 1.0f / mass;
        inertia = builder.inertia;
        invInertia = 1.0f / inertia;
        friction = builder.friction;
        restitution = builder.restitution;

        // debugging
        if (isDebugMode()) {
            lineVelocity = new Line.Builder(getPosition(),
                    new PointF(getPositionVector().add(getVelocity())))
                    .color(Color.argb(255, 255, 255, 255)).visible(true).build();
            circleShape =
                    new Circle.Builder(shape.getPosition(), shape.getRadius())
                    .color(Color.argb(255, 255, 255, 255)).visible(true).build();
        }
    }

    /**
     *
     */
    @Override
    public void close() {

        super.close();

        if (lineVelocity != null) {
            lineVelocity.close();
        }
        if (circleShape != null) {
            circleShape.close();
        }
    }

    /**
     *
     */
    @Override
    public void update() {

        // Calculate next velocity
        setVelocity(estimateFutureVelocityUsingForce(force));
        setAngularVelocity(estimateFutureAngularVelocityUsingTorque(torque));

        // Update position & rotatio
        getPosition().add(new PointF(velocity));
        setRotation(getRotation() + angularVelocity);

        super.update();

        if (isUpdatePossible()) {
            force.reset();
            torque = 0.0f;
        }

        // Update shape before collision check
        shape.setPosition(new PointF(getPosition()));
        shape.setRotation(getRotation());
    }

    /**
     *
     */
    @Override
    public void commit() {

        super.commit();

        // debugging
        if (isDebugMode()) {
            lineVelocity.setPoints(getPosition(),
                    new PointF(getFuturePositionVector(getUpdatePeriod() * 4)));
            lineVelocity.commit();

            circleShape.setCenter(shape.getPosition());
            circleShape.setRadius(shape.getRadius());
            circleShape.commit();
        }
    }

    /**
     *
     * @param targetPosition
     * @param weight
     */
    public void seek(PointF targetPosition, float weight) {

        Vector2D targetVector = targetPosition.vectorize().subtract(getPositionVector());
        Vector2D desiredForce =
                targetVector.normalize().multiply(getMaxVelocity()).subtract(getVelocity())
                        .multiply(weight);
        addForce(desiredForce);
    }

    /**
     *
     * @param targetPosition
     * @param weight
     */
    public void flee(PointF targetPosition, float weight) {

        Vector2D targetVector = getPositionVector().subtract(targetPosition.vectorize());
        Vector2D desiredForce =
                targetVector.normalize().multiply(getMaxVelocity()).subtract(getVelocity())
                    .multiply(weight);
        addForce(desiredForce);
    }

    /**
     *
     * @param weight
     */
    public void wander(float angleChange, float wanderRadius, float weight) {

        Vector2D circleCenter = getVelocity().normalize().multiply(wanderRadius);
        Vector2D displacement = new Vector2D(0, -1).multiply(wanderRadius);
        displacement.rotate(wanderAngle);
        wanderAngle += (Math.random() * angleChange) - (angleChange * .5);
        Vector2D desiredForce = circleCenter.add(displacement).truncate(maxForce).multiply(weight);
        addForce(desiredForce);
    }

    /**
     *
     */
    public void restrict(PointF center, float radius) {

        if (getFuturePositionVector(getUpdatePeriod()).distance(center.vectorize()) > radius) {
            setForce(new Vector2D());
            setVelocity(new Vector2D());
            Vector2D centerDirection =
                    center.vectorize().subtract(getPositionVector()).normalize();
            setForce(centerDirection.multiply(maxForce));
        }
    }

    /**
     *
     */
    public void restrict(OffsetCoord offsetCoord) {

        Point futureGameCoord = new Point(new PointF(getFuturePositionVector(getUpdatePeriod())));
        OffsetCoord futureOffsetCoord = new OffsetCoord(futureGameCoord);
        if (!futureOffsetCoord.equals(offsetCoord)) {
            setForce(new Vector2D());
            setVelocity(new Vector2D());
            Vector2D centerDirection =
                    offsetCoord.toGameCoord().vectorize().subtract(getPositionVector()).normalize();
            setForce(centerDirection.multiply(maxForce));
        }
    }

    /**
     *
     * @param neighbors
     */
    public void separate(ArrayList neighbors, float range, float weight) {

        Vector2D totalForce = new Vector2D();
        int neighborCount = 0;

        for (java.lang.Object object: neighbors) {
            CollidableObject neighbor = (CollidableObject) object;
            if (neighbor != this) {
                float distance = neighbor.getPositionVector().distance(getPositionVector());
                if (distance < range) {
                    Vector2D force = getPositionVector().subtract(neighbor.getPositionVector());
                    totalForce.add(force);
                    neighborCount++;
                }
            }
        }

        if (neighborCount > 0) {
            Vector2D desiredForce =
                    totalForce.divide(neighborCount).truncate(maxForce).multiply(weight);
            addForce(desiredForce);
        }
    }

    /**
     *
     * @param force
     * @return
     */
    private Vector2D estimateFutureVelocityUsingForce(Vector2D force) {

        Vector2D acceleration = force.clone().truncate(maxForce).multiply(invMass)
                .divide(getUpdatePeriod());
        return getVelocity().clone().multiply(1.0f - friction).add(acceleration)
                .truncate(maxVelocity);
    }

    /**
     *
     * @param torque
     * @return
     */
    private float estimateFutureAngularVelocityUsingTorque(float torque) {

        torque = Math.min(maxTorque, torque);
        float angularAcceleration = torque * invInertia / getUpdatePeriod();
        float angularVelocity = getAngularVelocity();

        angularVelocity *= 1.0f - restitution;
        angularVelocity += angularAcceleration;

        return angularVelocity;
    }

    /**
     *
     * @param alpha
     */
    public void offset(PointF alpha) {

        getPosition().offset(alpha);
    }

    /**
     *
     */
    private void stopMoving() {

        velocity.reset();
    }

    /**
     *
     */
    private void stopRotating() {

        angularVelocity = 0.0f;
    }

    /**
     *
     */
    public void stop() {

        stopMoving();
        stopRotating();
    }

    /**
     *
     * @param numberOfUpdate
     * @return
     */
    public Vector2D getFuturePositionVector(int numberOfUpdate) {

        Vector2D position = getPositionVector();
        Vector2D velocity = estimateFutureVelocityUsingForce(force);

        for (int nUpdate = 0; nUpdate < numberOfUpdate; ++nUpdate) {
            position.add(velocity);
        }

        return position;
    }

    /**
     *
     * @return
     */
    public Vector2D getVelocity() {

        return velocity;
    }

    /**
     *
     * @param velocity
     */
    public void setVelocity(Vector2D velocity) {

        this.velocity = velocity;
    }

    /**
     *
     * @return
     */
    public Vector2D getForwardVector() {

        return velocity.clone().normalize();
    }

    /**
     *
     * @return
     */
    public float getAngularVelocity() {

        return angularVelocity;
    }

    /**
     *
     * @param angularVelocity
     */
    public void setAngularVelocity(float angularVelocity) {

        this.angularVelocity = angularVelocity;
    }

    /*
     *
     * @return
     */
    public float getMaxVelocity() {

        return maxVelocity;
    }

    /**
     *
     * @param maxVelocity
     */
    public void setMaxVelocity(float maxVelocity) {

        this.maxVelocity = maxVelocity;
    }

    /**
     *
     * @return
     */
    public float getMaxAngularVelocity() {

        return maxAngularVelocity;
    }

    /**
     *
     * @param maxAngularVelocity
     */
    public void setMaxAngularVelocity(float maxAngularVelocity) {

        this.maxAngularVelocity = maxAngularVelocity;
    }

    /*
     *
     * @return
     */
    public float getMaxForce() {

        return maxForce;
    }

    /**
     *
     * @param maxForce
     */
    public void setMaxForce(float maxForce) {

        this.maxForce = maxForce;
    }

    /**
     *
     * @return
     */
    public Shape getShape() {

        return shape;
    }

    /**
     *
     * @param shape
     */
    public void setShape(Shape shape) {

        this.shape = shape;
    }

    /**
     *
     * @return
     */
    public Vector2D getForce() {

        return force;
    }

    /**
     *
     * @param force
     */
    public void setForce(Vector2D force) {

        this.force = force;
    }

    /**
     *
     * @return
     */
    public float getTorque() {

        return torque;
    }

    /**
     *
     * @param torque
     */
    public void setTorque(float torque) {

        this.torque = torque;
    }

    /**
     *
     * @return
     */
    public float getMass() {

        return mass;
    }

    /**
     *
     * @param mass
     */
    public void setMass(float mass) {

        this.mass = mass;
        if (mass == 0.0f)
            invMass = 0.0f;
        else
            invMass = 1.0f / mass;
    }

    /**
     *
     * @return
     */
    public float getInvMass() {

        return invMass;
    }

    /**
     *
     * @return
     */
    public float getInertia() {

        return inertia;
    }

    /**
     *
     * @param inertia
     */
    public void setInertia(float inertia) {

        this.inertia = inertia;
        if (inertia == 0.0f)
            invInertia = 0.0f;
        else
            invInertia = 1.0f / inertia;
    }

    /**
     *
     * @return
     */
    public float getFriction()
    {
        return friction;
    }

    /**
     *
     * @param friction
     */
    public void setFriction(float friction) {

        this.friction = friction;
    }

    /**
     *
     * @return
     */
    public float getRestitution() {

        return restitution;
    }

    /**
     *
     * @param restitution
     */
    public void setRestitution(float restitution) {

        this.restitution = restitution;
    }

    /**
     *
     * @param force
     */
    public void addForce(Vector2D force) {

        this.force.add(force);
    }

    /**
     *
     * @param force
     * @param pos
     */
    public void addForce(Vector2D force, Vector2D pos) {

        addForce(force);
        addTorque(pos.cross(force));
    }

    /**
     *
     * @param torque
     */
    public void addTorque(float torque) {

        this.torque += torque;
    }

    /**
     *
     * @return
     */
    public boolean isCollisionChecked() {

        return collisionChecked;
    }

    /**
     *
     * @param collisionChecked
     */
    public void setCollisionChecked(boolean collisionChecked) {

        this.collisionChecked = collisionChecked;
    }

    /**
     *
     * @param targetObject
     */
    public void onCollisionOccurred(CollidableObject targetObject) {
        // To be implemented by an user
    }

    /**
     *
     * @param collisionEnabled
     */
    public void setCollisionEnabled(boolean collisionEnabled) {

        this.collisionEnabled = collisionEnabled;
    }

    /**
     *
     * @return
     */
    public boolean isCollisionEnabled() {

        return collisionEnabled;
    }

    private Vector2D velocity;
    private float angularVelocity;
    private float maxVelocity;
    private float maxAngularVelocity;

    private Shape shape;
    private Vector2D force;
    private float torque;
    private float maxForce;
    private float maxTorque;
    private float mass;
    private float inertia;
    private float friction;
    private float restitution;

    private float invMass;
    private float invInertia;
    private boolean collisionChecked = false;
    private boolean collisionEnabled = true;

    private float wanderAngle = 0.0f;

    // debugging
    private Line lineVelocity;
    public Circle circleShape;
}
