SRC_URI = "https://fossies.org/linux/misc/legacy/bzip2-1.0.6.tar.gz \
           file://fix-bunzip2-qt-returns-0-for-corrupt-archives.patch \
           file://configure.ac;subdir=${BP} \
           file://Makefile.am;subdir=${BP} \
           file://run-ptest"
