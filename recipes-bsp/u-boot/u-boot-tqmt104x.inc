FILESEXTRAPATHS_prepend := "${THISDIR}/u-boot-tqmt104x_v2015.07:"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=0507cd7da8e7ad6d6701926ec9b84c95"

SRC_URI = "git://git.denx.de/u-boot.git"
SRCREV = "33711bdd4a4dce942fb5ae85a68899a8357bdd94"
SRCBRANCH = "master"

PV_append = "-tqmt104x"
PR = "r1"

S = "${WORKDIR}/git"

do_patch() {
	cd "${S}"
	for p in "${WORKDIR}"/*.patch; do
		git apply "$p"
	done
}

do_compile_prepend() {
	unset LDFLAGS
	unset CFLAGS
	unset CPPFLAGS

	printf "%s" "${LOCALVERSION}" > ${S}/.scmversion
	printf "%s" "${LOCALVERSION}" > ${B}/.scmversion
}

### Patch series -- DO NOT EDIT BELOW THIS LINE!
LOCALVERSION = "-g69ca8d8"
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
SRC_URI += "file://0029-TQMT1042-Add-SD-card-boot-command.patch"
SRC_URI += "file://0030-TQMT1042-Add-NOR-boot-command.patch"
SRC_URI += "file://0031-cosmetic-TQMT1042-tqmt1042.c-split-comment-into-two-.patch"
SRC_URI += "file://0032-TQMT1042-Set-MAC-GTX-Clock-to-be-always-provided-by-.patch"
SRC_URI += "file://0033-cosmetic-TQMT1042-tqmt1042.h-move-comment-to-seperat.patch"
SRC_URI += "file://0034-TQMT1040-automatic-reprogramming-of-ethernet-phy-if-.patch"
SRC_URI += "file://0035-cosmetic-TQMT1042-eth.c-minor-edit-of-commentary-for.patch"
SRC_URI += "file://0036-TQMT1042-corrected-error-in-reset-configuration-word.patch"
SRC_URI += "file://0037-TQMT1042-add-reset-configuration-word-for-SerDes-con.patch"
