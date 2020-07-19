//
// Created by crims on 2020-04-19.
//

#pragma once

#include <string>
#include <unordered_map>
#include <memory>
#include <mutex>
#include <GLES3/gl3.h>
#include "Texture.h"

namespace Engine2D {

class ResourceManager final
{
    struct ConstructorTag {};

public:
    explicit ResourceManager(ConstructorTag) {}

    static ResourceManager *getInstance();

    void releaseAllTextures();

public:
    std::shared_ptr<Texture> loadTexture(const GLchar *file, GLboolean alpha, GLboolean smooth, std::string name);
    std::shared_ptr<Texture> loadTexture(const unsigned char *memory, size_t memSize, GLboolean alpha,
            GLboolean smooth, std::string name);
    std::shared_ptr<Texture> attachTexture(std::string name);
    void releaseTexture(std::string name);
    std::shared_ptr<Texture> getTexture(std::string name);

private:
    std::unordered_map<std::string, std::pair<std::shared_ptr<Texture>, uint32_t> > mTextures;
    std::mutex mTextureLock;
};

}
