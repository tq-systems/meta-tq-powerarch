require recipes-bsp/u-boot/u-boot.inc

DESCRIPTION = "U-Boot for TQMT2081 modules"

require u-boot-tqmt2081.inc

inherit deploy

PROVIDES += "virtual/bootloader u-boot"

PACKAGE_ARCH = "${MACHINE_ARCH}"

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

do_configure_prepend() {
	if [ "x${UBOOT_CONFIG}" != "x" ] && [ "x${FSL_RCW}" != "x" ]; then
		for config in ${UBOOT_MACHINE}; do
			sed -i '/CONFIG_RCW_CFG/d' ${S}/configs/${config}
			echo "CONFIG_RCW_CFG_${FSL_RCW}=y" >> ${S}/configs/${config}
		done
	fi
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

