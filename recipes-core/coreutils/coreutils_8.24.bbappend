SRC_URI = "${GNU_MIRROR}/coreutils/${BP}.tar.xz;name=tarball \
           https://dev.gentoo.org/~vapier/dist/coreutils-8.24-man.tar.xz;name=manpages \
           file://man-decouple-manpages-from-build.patch \
           file://remove-usr-local-lib-from-m4.patch \
           file://fix-selinux-flask.patch \
           file://0001-Unset-need_charset_alias-when-building-for-musl.patch \
          "
