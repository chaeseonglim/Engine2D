//
// Created by crims on 2020-04-19.
//

#pragma once

#include <GLES3/gl3.h>

namespace Engine2D {

class Texture final
{
public:
    Texture(const GLchar *file, bool alpha, bool smooth);
    Texture(const unsigned char *memory, size_t memSize, bool alpha, bool smooth);
    ~Texture();

    void prepare(unsigned char* data);
    void cleanup();
    void bind();

    GLuint id() const { return mID; }
    bool isLoaded() const { return mLoaded; }

    GLuint getWidth() const {
        return mWidth;
    }

    GLuint getHeight() const {
        return mHeight;
    }

    GLuint getWrapS() const {
        return mWrapS;
    }

    void setWrapS(GLuint mWrapS) {
        Texture::mWrapS = mWrapS;
    }

    GLuint getWrapT() const {
        return mWrapT;
    }

    void setWrapT(GLuint mWrapT) {
        Texture::mWrapT = mWrapT;
    }

    GLuint getFilterMin() const {
        return mFilterMin;
    }

    void setFilterMin(GLuint mFilterMin) {
        Texture::mFilterMin = mFilterMin;
    }

    GLuint getFilterMax() const {
        return mFilterMax;
    }

    void setFilterMax(GLuint mFilterMax) {
        Texture::mFilterMax = mFilterMax;
    }

private:
    void loadFromFile(const GLchar *file, GLboolean alpha);
    void loadFromMemory(const unsigned char *memory, size_t memSize, GLboolean alpha);

private:
    // Texture ID
    GLuint mID;
    // Texture image dimensions
    GLuint mWidth, mHeight; // Width and height of loaded image in pixels
    bool mAlpha; // Alphaness of image
    bool mSmooth; // Smoothness of image
    // Texture Format
    GLuint mInternalFormat = GL_RGB; // Format of texture object
    GLuint mImageFormat = GL_RGB; // Format of loaded image
    // Texture configuration
    GLuint mWrapS = GL_CLAMP_TO_EDGE; // Wrapping mode on S axis
    GLuint mWrapT = GL_CLAMP_TO_EDGE; // Wrapping mode on T axis
    GLuint mFilterMin = GL_LINEAR; // Filtering mode if texture pixels < screen pixels
    GLuint mFilterMax = GL_LINEAR; // Filtering mode if texture pixels > screen pixels
    bool mLoaded = false;
    bool mPrepared = false;
    uint8_t* mImageBuffer = nullptr;
};

}

