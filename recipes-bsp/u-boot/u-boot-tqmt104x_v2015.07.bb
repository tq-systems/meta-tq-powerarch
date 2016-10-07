require recipes-bsp/u-boot/u-boot.inc

DESCRIPTION = "U-Boot for TQMT104x modules"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=0507cd7da8e7ad6d6701926ec9b84c95"

SRC_URI = "git://git.denx.de/u-boot.git"
SRCREV = "33711bdd4a4dce942fb5ae85a68899a8357bdd94"
SRCBRANCH = "master"

PV_append = "-tqmt104x"
PR = "r1"

inherit deploy

PROVIDES += "virtual/bootloader u-boot"

PACKAGE_ARCH = "${MACHINE_ARCH}"

S = "${WORKDIR}/git"

python () {
    if d.getVar("TCMODE", True) == "external-fsl":
        return

    ml = d.getVar("MULTILIB_VARIANTS", True)
    arch = d.getVar("OVERRIDES", True)

    if "e5500-64b:" in arch or "e6500-64b:" in arch:
        if not "lib32" in ml:
            raise bb.parse.SkipPackage("Building the u-boot for this arch requires multilib to be enabled")
        sys_multilib = 'powerpc' + d.getVar('TARGET_VENDOR') + 'mllib32-' + d.getVar('HOST_OS')
        d.setVar('DEPENDS_append', ' lib32-gcc-cross-powerpc lib32-libgcc')
        d.setVar('PATH_append', ':' + d.getVar('STAGING_BINDIR_NATIVE') + '/' + sys_multilib)
        d.setVar('TOOLCHAIN_OPTIONS_append', '/../lib32-' + d.getVar("MACHINE"))
        d.setVar("WRAP_TARGET_PREFIX", sys_multilib + '-')
}

WRAP_TARGET_PREFIX ?= "${TARGET_PREFIX}"

UBOOT_MAKE_TARGET_append = "${@d.getVar('FSL_RCW', True) and ' fsl_rcw.bin' or ''}"

EXTRA_OEMAKE = 'CROSS_COMPILE=${WRAP_TARGET_PREFIX} CC="${WRAP_TARGET_PREFIX}gcc ${TOOLCHAIN_OPTIONS}"'

do_patch() {
	cd "${S}"
	for p in "${WORKDIR}"/*.patch; do
		git apply "$p"
	done
}

do_configure_prepend() {
	if [ "x${UBOOT_CONFIG}" != "x" ] && [ "x${FSL_RCW}" != "x" ]; then
		for config in ${UBOOT_MACHINE}; do
			sed -i '/CONFIG_RCW_CFG/d' ${S}/configs/${config}
			echo "CONFIG_RCW_CFG_${FSL_RCW}=y" >> ${S}/configs/${config}
		done
	fi
}

do_compile_prepend() {
	unset LDFLAGS
	unset CFLAGS
	unset CPPFLAGS

	printf "%s" "${LOCALVERSION}" > ${S}/.scmversion
	printf "%s" "${LOCALVERSION}" > ${B}/.scmversion
}

do_deploy_append() {
	# For sdcard boot, we don't use u-boot.bin, but u-boot-with-spl-pbl.bin
	if [ "x${UBOOT_CONFIG}" != "x" ]; then
		for config in ${UBOOT_MACHINE}; do
			i=`expr $i + 1`;
			for type in ${UBOOT_CONFIG}; do
				j=`expr $j + 1`;
				if [ $j -eq $i ] && [ "${type}" = "sdcard" ]; then
					install -d ${DEPLOYDIR}
					install ${S}/${config}/u-boot-with-spl-pbl.${UBOOT_SUFFIX} ${DEPLOYDIR}/u-boot-${type}-${PV}-${PR}.${UBOOT_SUFFIX}
				fi
			done
			unset  j
		done
		unset  i
	fi

	# Install RCW
	if [ "x${UBOOT_CONFIG}" != "x" ] && [ "x${FSL_RCW}" != "x" ]; then
		for config in ${UBOOT_MACHINE}; do
			i=`expr $i + 1`;
			for type in ${UBOOT_CONFIG}; do
				j=`expr $j + 1`;
				if [ $j -eq $i ]; then
					install -d ${DEPLOYDIR}
					install -v ${S}/${config}/fsl_rcw.bin ${DEPLOYDIR}/fsl_rcw-${type}-${FSL_RCW}.bin
				fi
			done
			unset j
		done
		unset i
	fi
}

FILESEXTRAPATHS_prepend := "${THISDIR}/u-boot-tqmt104x_v2015.07:"

### Patch series -- DO NOT EDIT BELOW THIS LINE!
LOCALVERSION = "-g443307b"
SRC_URI += "file://0001-Add-support-for-PCF85063-RTC-chip-of-NXP-semiconduct.patch"
SRC_URI += "file://0002-Add-initial-board-support-for-TQMT1042.patch"
SRC_URI += "file://0003-Modified-board-configuration-TQMT1042.h.patch"
SRC_URI += "file://0004-Updated-RCW-to-support-100MHz-on-SD1_REF_CLK1-input.patch"
SRC_URI += "file://0005-edit-RCW-to-set-UART2-pins-to-UART-functionality-ins.patch"
SRC_URI += "file://0006-edit-RCW-to-set-FM1_MAC_RAT-as-working-with-1G-only.patch"
SRC_URI += "file://0007-add-DIU-intitialisation-remove-unused-define-CONFIG_.patch"
SRC_URI += "file://0008-add-missing-SerDes-RCW-configuration-option-0x88-in-.patch"
SRC_URI += "file://0009-set-RGMII-delay-values-in-ethernet-phys-located-on-t.patch"
SRC_URI += "file://0010-remove-pbi-workaround-for-erratum-A-008007-silicon-h.patch"
SRC_URI += "file://0011-powerpc-T104xD4-Add-Secure-boot-support-for-T104xD4R.patch"
SRC_URI += "file://0012-add-RCW-serdes-protocol-configuration-0x8E.patch"
SRC_URI += "file://0013-add-device-specific-initialisation-routines-for-SGMI.patch"
SRC_URI += "file://0014-TQMT1042-add-ethernet-debug-outputs-to-visualise-eth.patch"
SRC_URI += "file://0015-corrected-typo-introduced-in-commit-222a655dd50c826e.patch"
SRC_URI += "file://0016-TQMT1042-add-automatic-feature-to-reprogram-the-serd.patch"
SRC_URI += "file://0017-u-boot-mpc85xx-u-boot-.lds-remove-_GLOBAL_OFFSET_TAB.patch"
SRC_URI += "file://0018-add-fgnu89-inline-option-for-gcc5.patch"
SRC_URI += "file://0019-Modified-mkimage-to-build-RCW-binaries.patch"
SRC_URI += "file://0020-Added-RCW-mkimage-defines-for-NOR-flash-boot.patch"
SRC_URI += "file://0021-TQMT1042-set-88E1340-phy-reset-duration-to-10ms-inst.patch"
SRC_URI += "file://0022-TQMT1042-introduce-RCWs-for-Serdes-configurations-0x.patch"
SRC_URI += "file://0023-fsl_rcw.bin-Fix-stand-alone-build.patch"
SRC_URI += "file://0024-TQMT1042-edit-RCWs-to-set-pin-muxing-to-DIU.patch"
SRC_URI += "file://0025-remove-old-RCW-cfg-file.patch"
SRC_URI += "file://0026-set-TQMT1042-default-environment.patch"
SRC_URI += "file://0027-TQMT1042-increase-U-Boot-I-O-buffer-size-for-conveni.patch"
SRC_URI += "file://0028-cosmetic-TQMT1042-Rename-RCW-configuration-constants.patch"
