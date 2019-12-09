
#include "file_mapper.h"

FileMapper* createReadOnlyFileMapper(const char* filename) {
    
    FileMapper *fm;
    struct stat st;
    
    fm = (FileMapper*) malloc(sizeof(FileMapper));
    
    fm->fd = open(filename, O_RDONLY);
    if (fm->fd == -1) {
        //fprintf(stderr, "Error: Open file failed when create file mapper\n");
        free(fm);
        return NULL;
    }
    
    fstat(fm->fd, &st);
    fm->size = st.st_size;
    
    fm->data = (unsigned char*) mmap(NULL, fm->size, PROT_READ, MAP_SHARED, fm->fd, 0);
    
    return fm;
}

void unmapFileMapper(FileMapper *fm) {
    if (fm) {
        munmap(fm->data, fm->size);
        close(fm->fd);
        free(fm);
    }
}
