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

EXTRA_OEMAKE = 'CROSS_COMPILE=${WRAP_TARGET_PREFIX} CC="${WRAP_TARGET_PREFIX}gcc ${TOOLCHAIN_OPTIONS}"'

do_patch() {
	cd "${S}"
	for p in "${WORKDIR}"/*.patch; do
		git apply "$p"
	done
}

do_configure_prepend() {
        printf "Append RCW selection to _defconfig (CONFIG_SYS_FSL_RCW)\n"
        printf "FSL_RCW: "
        echo ${FSL_RCW}
        if [ "${FSL_RCW}" = "SERDES88" ]; then
                # change RCW in U-Boot to SERDES88
                printf "Configuration SERDES88 selected\n"
                sed -i '/RCW_CFG/d' ${S}/configs/TQMT1042_defconfig
                echo "CONFIG_TQMT1042_RCW_CFG_SERDES88=y" >> ${S}/configs/TQMT1042_defconfig
        else
                # change RCW in U-Boot to SERDES86 (default)
                printf "Configuration SERDES86 (default) selected\n"
                sed -i '/RCW_CFG/d' ${S}/configs/TQMT1042_defconfig
                echo "CONFIG_TQMT1042_RCW_CFG_SERDES86=y" >> ${S}/configs/TQMT1042_defconfig
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
    install -d ${DEPLOYDIR}/rcw
    cp -r ${S}/TQMT1042_defconfig/fsl_rcw.bin ${DEPLOYDIR}/rcw/
    cp  ${S}/TQMT1042_SDCARD_defconfig/u-boot-with-spl-pbl.bin ${DEPLOYDIR}
    ln -srf ${DEPLOYDIR}/u-boot-with-spl-pbl.bin ./u-boot.bin-sdcard
    ln -srf ${DEPLOYDIR}/u-boot-with-spl-pbl.bin ./u-boot-tqmt1042-64b-stk.bin-sdcard
}

FILESEXTRAPATHS_prepend := "${THISDIR}/u-boot-tqmt104x_v2015.07:"

### Patch series -- DO NOT EDIT BELOW THIS LINE!
LOCALVERSION = "-g138c843"
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
