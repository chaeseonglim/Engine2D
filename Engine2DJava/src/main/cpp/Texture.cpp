//
// Created by crims on 2020-04-19.
//

#include "Texture.h"
#include "Log.h"
#include "Renderer.h"
#include "Utils.h"

#include <SOIL.h>

#define LOG_TAG "Texture"

namespace Engine2D {

Texture::Texture(const GLchar *file, bool alpha, bool smooth)
        : mAlpha(alpha)
        , mSmooth(smooth)
{
    if (mAlpha) {
        mInternalFormat = GL_RGBA;
        mImageFormat = GL_RGBA;
    }
    if (!mSmooth) {
       mFilterMax = GL_NEAREST;
       mFilterMin = GL_NEAREST;
    }

    loadFromFile(file, (GLboolean)alpha);
}

Texture::Texture(const unsigned char *memory, size_t memSize, bool alpha, bool smooth)
        : mAlpha(alpha)
        , mSmooth(smooth)
{
    if (mAlpha) {
        mInternalFormat = GL_RGBA;
        mImageFormat = GL_RGBA;
    }
    if (!mSmooth) {
        mFilterMax = GL_NEAREST;
        mFilterMin = GL_NEAREST;
    }

    loadFromMemory(memory, memSize, (GLboolean)alpha);
}

Texture::~Texture()
{
    cleanup();
}

void Texture::cleanup() {
    if (mPrepared) {
        Renderer::getInstance()->run([id = this->mID]() {
            glDeleteTextures(1, &id);
        });
        mPrepared = false;
    }
}

void Texture::loadFromFile(const GLchar *file, GLboolean alpha)
{
    // Load image
    int width, height;

    unsigned char* image = SOIL_load_image(file, &width, &height, 0,
            alpha ? SOIL_LOAD_RGBA : SOIL_LOAD_RGB);
    if (image == nullptr) {
        ALOGE("Failed to load %s", file);
        return;
    }

    // Prepare texture
    mWidth = width;
    mHeight = height;
    mLoaded = true;
    mImageBuffer = image;
    Renderer::getInstance()->run([this]() {
        if (!mPrepared) {
            prepare(mImageBuffer);
            SOIL_free_image_data(mImageBuffer);
            mImageBuffer = nullptr;
        }
    });
}

void Texture::loadFromMemory(const unsigned char *memory, size_t memSize, GLboolean alpha)
{
    // Load image
    int width, height;
    unsigned char* image = SOIL_load_image_from_memory(memory, memSize, &width, &height, 0,
            alpha ? SOIL_LOAD_RGBA : SOIL_LOAD_RGB);

    if (image == nullptr) {
        ALOGE("Failed to load image from memory");
        return;
    }

    // Prepare texture
    mWidth = width;
    mHeight = height;
    mLoaded = true;
    mImageBuffer = image;
    Renderer::getInstance()->run([this]() {
        if (!mPrepared) {
            prepare(mImageBuffer);
            SOIL_free_image_data(mImageBuffer);
            mImageBuffer = nullptr;
        }
    });
}

void Texture::prepare(unsigned char* data)
{
    if (mLoaded && !mPrepared) {
        bool error = false;

        glGenTextures(1, &mID);
        error &= checkGlError("glGenTexture");

        // Create Texture
        glBindTexture(GL_TEXTURE_2D, mID);
        error &= checkGlError("glBindTexture");
        glTexImage2D(GL_TEXTURE_2D, 0, mInternalFormat, mWidth, mHeight, 0, mImageFormat,
                     GL_UNSIGNED_BYTE, data);
        error &= checkGlError("glTexImage2D");

        // Set Texture wrap and filter modes
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, mWrapS);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, mWrapT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, mFilterMin);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, mFilterMax);
        error &= checkGlError("glTexParameteri");

        // Unbind texture
        glBindTexture(GL_TEXTURE_2D, 0);
        error &= checkGlError("glBindTexture");

        if (!error) {
            mPrepared = true;
        } else {
            ALOGW("Failed to prapare texture");
        }
    }
}

void Texture::bind()
{
    if (!mPrepared) {
        prepare(mImageBuffer);
        SOIL_free_image_data(mImageBuffer);
        mImageBuffer = nullptr;
    }

    if (mPrepared) {
        // Bind texture
        glBindTexture(GL_TEXTURE_2D, mID);

        // Set Texture wrap and filter modes
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, mWrapS);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, mWrapT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, mFilterMin);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, mFilterMax);
    }
}

}