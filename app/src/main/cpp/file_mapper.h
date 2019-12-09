
#ifndef MACE_EXAMPLES_ANDROID_MACELIBRARY_SRC_MAIN_CPP_IMAGE_FILE_MAPPER_H_
#define MACE_EXAMPLES_ANDROID_MACELIBRARY_SRC_MAIN_CPP_IMAGE_FILE_MAPPER_H_

#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <sys/types.h>

struct FileMapper {
    int fd;
    int size;
    unsigned char *data;
};

FileMapper* createReadOnlyFileMapper(const char* filename);
void unmapFileMapper(FileMapper *fm);

#endif
