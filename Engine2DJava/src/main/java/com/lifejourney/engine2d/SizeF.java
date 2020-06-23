package com.lifejourney.engine2d;

public class SizeF {

    public SizeF() {
        width = 0;
        height = 0;
    }

    public SizeF(Size size) {
        width = (float)size.width;
        height = (float)size.height;
    }

    public SizeF(SizeF size) {
        width = size.width;
        height = size.height;
    }

    public SizeF(float width, float height) {
        this.width = width;
        this.height = height;
    }

    /**
     *
     * @return
     */
    public SizeF clone() {
        return new SizeF(this);
    }

    /**
     *
     * @param w
     * @param h
     * @return
     */
    public SizeF add(float w, float h) {
        width += w;
        height += h;
        return this;
    }

    /**
     *
     * @param m
     * @return
     */
    public SizeF multiply(float m) {
        width *= m;
        height *= m;
        return this;
    }

    /**
     *
     * @param w
     * @param h
     * @return
     */
    public SizeF multiply(float w, float h) {
        width *= w;
        height *= h;
        return this;
    }

    public float width;
    public float height;
}
