/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#pragma once

#include <array>
#include <GLES3/gl3.h>
#include <glm/glm.hpp>
#include <mutex>
#include <vector>
#include "Texture.h"

namespace Engine2D
{

class Sprite
{
public:
    Sprite() = default;
    Sprite(const std::shared_ptr<Texture>& texture, int gridCols = 1, int gridRows = 1);
    virtual ~Sprite();

    void draw(const glm::mat4 &projection, const glm::mat4 &initialModel);

    void show() {
        mVisible = true;
    }
    void hide() {
        mVisible = false;
    }
    void setVisible(bool visible) {
        mVisible = visible;
    }
    bool isVisible() const {
        return mVisible;
    }
    const glm::vec2 &getPosition() const {
        return mPos;
    }
    void setPosition(const glm::vec2 &pos) {
        Sprite::mPos = pos;
    }
    const glm::vec2 &getSize() const {
        return mSize;
    }
    void setSize(const glm::vec2 &size) {
        Sprite::mSize = size;
    }
    GLfloat getRotation() const {
        return mRotation;
    }
    void setRotation(GLfloat rotation) {
        Sprite::mRotation = rotation;
    }
    GLfloat getOpaque() const {
        return mOpaque;
    }

    void setOpaque(GLfloat mOpaque) {
        Sprite::mOpaque = mOpaque;
    }
    const int getLayer() const {
        return mLayer;
    }
    void setLayer(int layer) {
        mLayer = layer;
    }

    const GLfloat getDepth() const {
        return mDepth;
    }
    void setDepth(GLfloat depth) {
        mDepth = depth;
    }

    const void setGridIndex(int gridCol, int gridRow);

    int getGridRows() const {
        return mGridRows;
    }

    int getGridCols() const {
        return mGridCols;
    }

    int getCurGridRow() const {
        return mCurGridRow;
    }

    int getCurGridCol() const {
        return mCurGridCol;
    }

    void setColorize(const glm::vec3& colorize) {
        mColorize = colorize;
    }

private:
    struct ProgramState {

        enum ProgramType {
            PROGRAM_OPAQUE,
            PROGRAM_OPAQUE_COLORIZE,
            PROGRAM_TRANSPARENT,
            PROGRAM_TRANSPARENT_COLORIZE,
            PROGRAM_TYPE_COUNT
        };

        struct Program {
            GLuint program;
            GLint modelHandle;
            GLint projectionHandle;
            GLint opaqueHandle;
            GLint colorizeHandle;
        };

        ProgramState();

        Program programs[PROGRAM_TYPE_COUNT];
    };
    static std::unique_ptr<ProgramState> sProgramState;

    void prepare();
    void prepareInternal();
    void cleanup();

public:
    static bool initProgram();

private:
    glm::vec2 mPos;
    glm::vec2 mSize;
    int mLayer = 0;
    GLfloat mDepth = 0.0f;
    GLfloat mRotation = 0.0f;
    GLfloat mOpaque = 1.0f;
    glm::vec3 mColorize = glm::vec3(1.0f, 1.0f, 1.0f);
    std::shared_ptr<Texture> mTexture;
    GLuint mQuadVAO;
    GLuint mVBO;

    int mGridCols = 1;
    int mGridRows = 1;
    int mCurGridCol = 0;
    int mCurGridRow = 0;

    bool mPrepared = false;
    bool mNeedPrepareAgain = false;
    bool mVisible = false;
};

} // namespace samples
