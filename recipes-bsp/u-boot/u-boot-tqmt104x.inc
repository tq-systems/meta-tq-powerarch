FILESEXTRAPATHS_prepend := "${THISDIR}/u-boot-tqmt104x_v2015.07:"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=0507cd7da8e7ad6d6701926ec9b84c95"

SRC_URI = "${TQ_GIT_BASEURL}/uboot-tqmt.git;protocol=${TQ_GIT_PROTOCOL};branch=${SRCBRANCH}"

SRCREV = "3b05a70f79035a19b796c191b1bc7114fcae97ba"
SRCBRANCH = "TQMTxxxx-u-boot-v2015.07"

PV_append = "-tqmt104x"
PR = "r1"

S = "${WORKDIR}/git"

do_compile_prepend() {
	unset LDFLAGS
	unset CFLAGS
	unset CPPFLAGS

	printf "%s" "${LOCALVERSION}" > ${S}/.scmversion
	printf "%s" "${LOCALVERSION}" > ${B}/.scmversion
}
