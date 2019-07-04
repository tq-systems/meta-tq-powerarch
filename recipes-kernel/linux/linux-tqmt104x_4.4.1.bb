SECTION = "kernel"
DESCRIPTION = "Linux kernel for TQMT104x modules"
LICENSE = "GPLv2"

# make everything compatible for the time being
COMPATIBLE_MACHINE_$MACHINE = "$MACHINE"

FILESEXTRAPATHS_prepend := "${THISDIR}/linux-tqmt104x_4.4.1:"

LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

inherit kernel
include recipes-kernel/linux/linux-dtb.inc

KERNEL_IMAGETYPE = "uImage"

LINUX_VERSION = "4.4.1"
LOCALVERSION = "-tqmt104x"

SRC_URI = "${TQ_GIT_BASEURL}/linux-tqmt.git;protocol=${TQ_GIT_PROTOCOL};branch=${SRCBRANCH}"

SRCBRANCH = "TQMTxxxx-linux-v4.4"
SRCREV = "9e59f4ba56c71b1655f82915806a42c22df9dd18"

PV = "${LINUX_VERSION}-tqmt104x"
PR = "r1"

S = "${WORKDIR}/git"

DEPENDS_append = " libgcc"
# Do not put Images into /boot of rootfs, install kernel-image if needed
RDEPENDS_kernel-base = ""

KERNEL_CC_append = " ${TOOLCHAIN_OPTIONS}"
KERNEL_LD_append = " ${TOOLCHAIN_OPTIONS}"

do_configure_prepend() {
	echo "" > ${B}/.config
	echo "CONFIG_LOCALVERSION=\"${LOCALVERSION}\"" >> ${B}/.config
	echo "CONFIG_LOCALVERSION_AUTO=y" >> ${B}/.config
	cat "${KERNEL_DEFCONFIG}" | sed \
		-e '/CONFIG_LOCALVERSION[ =]/d' \
		-e '/CONFIG_LOCALVERSION_AUTO[ =]/d' \
	>> ${B}/.config || exit 1
}
