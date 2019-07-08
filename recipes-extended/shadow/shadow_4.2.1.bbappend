SRC_URI = "https://distfiles.pld-linux.org/distfiles/by-md5/2/b/2bfafe7d4962682d31b5eba65dba4fc8/${BPN}-${PV}.tar.xz \
           file://shadow-4.1.3-dots-in-usernames.patch \
           file://usermod-fix-compilation-failure-with-subids-disabled.patch \
           file://fix-installation-failure-with-subids-disabled.patch \
           file://0001-Do-not-read-login.defs-before-doing-chroot.patch \
           file://check_size_of_uid_t_and_gid_t_using_AC_CHECK_SIZEOF.patch \
           ${@bb.utils.contains('PACKAGECONFIG', 'pam', '${PAM_SRC_URI}', '', d)} \
           "

SRC_URI[md5sum] = "2bfafe7d4962682d31b5eba65dba4fc8"
SRC_URI[sha256sum] = "3b0893d1476766868cd88920f4f1231c4795652aa407569faff802bcda0f3d41"
