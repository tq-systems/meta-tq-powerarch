SECTION = "kernel"
DESCRIPTION = "linux kernel for TQMT2081 modules"
LICENSE = "GPLv2"

# make everything compatible for the time being
COMPATIBLE_MACHINE_$MACHINE = "$MACHINE"

FILESEXTRAPATHS_prepend := "${THISDIR}/linux-tqmt2081_4.4.1:"

LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

inherit kernel
include recipes-kernel/linux/linux-dtb.inc

KERNEL_IMAGETYPE = "uImage"

LINUX_VERSION = "4.4.1"
LOCALVERSION = "-tqmt2081"

SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/stable/linux-stable.git;branch=linux-4.4.y"
SRCREV = "f1ab5eafa3625b41c74326a1994a820ff805d5b2"

PV = "${LINUX_VERSION}-tqmt2081"
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

### Patch series -- DO NOT EDIT BELOW THIS LINE!
LOCALVERSION .= "-gecd51ee"
SRC_URI += "file://0001-fsl-fman-Add-FMan-MURAM-support.patch"
SRC_URI += "file://0002-fsl-fman-Add-FMan-support.patch"
SRC_URI += "file://0003-fsl-fman-Add-FMan-MAC-support.patch"
SRC_URI += "file://0004-fsl-fman-Add-FMan-SP-support.patch"
SRC_URI += "file://0005-fsl-fman-Add-FMan-Port-Support.patch"
SRC_URI += "file://0006-fsl-fman-Add-FMan-MAC-driver.patch"
SRC_URI += "file://0007-fsl-fman-allow-modular-build.patch"
SRC_URI += "file://0008-fsl-fman-fix-the-pause_time-test.patch"
SRC_URI += "file://0009-fsl-fman-double-free-on-probe-failure.patch"
SRC_URI += "file://0010-apply-patch-from-Freescale-SDK-85281d5586856958c948f.patch"
SRC_URI += "file://0011-add-define-for-TQ-Systems-TQMT1042-SOC-in-corenet_ge.patch"
SRC_URI += "file://0012-add-preliminary-device-tree-support-and-defconfig-fo.patch"
SRC_URI += "file://0013-powerpc-Fix-incorrect-PPC32-PAMU-dependency.patch"
SRC_URI += "file://0014-powerpc-mpc85xx-Add-MDIO-bus-muxing-support-to-the-P.patch"
SRC_URI += "file://0015-powerpc-mpc85xx-Add-TMU-device-tree-support-for-T102.patch"
SRC_URI += "file://0016-powerpc-mpc85xx-Add-MDIO-bus-muxing-support-to-the-b.patch"
SRC_URI += "file://0017-powerpc-fsl-dts-Add-jedec-spi-nor-flash-compatible.patch"
SRC_URI += "file://0018-powerpc-mpc85xx-add-cell-index-entries-to-the-BMan-p.patch"
SRC_URI += "file://0019-powerpc-mpc85xx-Add-DPAA-Ethernet-QMan-support-to-th.patch"
SRC_URI += "file://0020-powerpc-fsl-booke-Add-T1023RDB-QBMan-device-tree-com.patch"
SRC_URI += "file://0021-powerpc-fsl-booke-Add-T1024RDB-QBMan-device-tree-com.patch"
SRC_URI += "file://0022-powerpc-mpc85xx-Add-TMU-device-tree-support-for-T104.patch"
SRC_URI += "file://0023-powerpc-fsl-Use-new-clockgen-binding.patch"
SRC_URI += "file://0024-powerpc-re-add-devm_ioremap_prot.patch"
SRC_URI += "file://0025-soc-tegra-Provide-per-SoC-Kconfig-symbols.patch"
SRC_URI += "file://0026-ARM-bcm2835-add-rpi-power-domain-driver.patch"
SRC_URI += "file://0027-genalloc-support-memory-allocation-with-bytes-alignm.patch"
SRC_URI += "file://0028-genalloc-support-allocating-specific-region.patch"
SRC_URI += "file://0029-CPM-QE-use-genalloc-to-manage-CPM-QE-muram.patch"
SRC_URI += "file://0030-QE-CPM-move-muram-management-functions-to-qe_common.patch"
SRC_URI += "file://0031-QE-use-subsys_initcall-to-init-qe.patch"
SRC_URI += "file://0032-ported-QE-Move-QE-from-arch-powerpc-to-drivers-soc-c.patch"
SRC_URI += "file://0033-reset-add-of_reset_control_get_by_index.patch"
SRC_URI += "file://0034-reset-Fix-of_reset_control_get-for-consistent-return.patch"
SRC_URI += "file://0035-ARM-STi-Add-DT-defines-for-co-processor-reset-lines.patch"
SRC_URI += "file://0036-reset-sti-Add-support-for-resetting-co-processors.patch"
SRC_URI += "file://0037-reset-sti-Provide-ops-.status-call-back.patch"
SRC_URI += "file://0038-reset-sti-add-a-missing-blank-line-after-declaration.patch"
SRC_URI += "file://0039-reset-sunxi-mark-the-of_device_id-array-as-__initcon.patch"
SRC_URI += "file://0040-reset-use-ENOTSUPP-instead-of-ENOSYS.patch"
SRC_URI += "file://0041-reset-remove-redundant-CONFIG_RESET_CONTROLLER-from-.patch"
SRC_URI += "file://0042-reset-check-return-value-of-reset_controller_registe.patch"
SRC_URI += "file://0043-reset-remove-unused-device-pointer-from-struct-reset.patch"
SRC_URI += "file://0044-reset-hisilicon-document-hisi-hi6220-reset-controlle.patch"
SRC_URI += "file://0045-reset-hi6220-Reset-driver-for-hisilicon-hi6220-SoC.patch"
SRC_URI += "file://0046-arm64-dts-Add-reset-dts-config-for-Hisilicon-Hi6220-.patch"
SRC_URI += "file://0047-reset-ath79-Add-system-restart-support.patch"
SRC_URI += "file://0048-Documentation-dt-add-bindings-for-TI-Wakeup-M3-IPC-d.patch"
SRC_URI += "file://0049-soc-ti-Add-wkup_m3_ipc-driver.patch"
SRC_URI += "file://0050-soc-qcom-Introduce-common-SMEM-state-machine-code.patch"
SRC_URI += "file://0051-soc-qcom-smsm-Add-driver-for-Qualcomm-SMSM.patch"
SRC_URI += "file://0052-soc-qcom-smp2p-Qualcomm-Shared-Memory-Point-to-Point.patch"
SRC_URI += "file://0053-serial-msm_serial-Make-config-tristate.patch"
SRC_URI += "file://0054-soc-qcom-enable-smsm-smp2p-modular-build.patch"
SRC_URI += "file://0055-MAINTAINERS-Add-rules-for-Qualcomm-dts-files.patch"
SRC_URI += "file://0056-ARM-qcom-select-ARM_CPU_SUSPEND-for-power-management.patch"
SRC_URI += "file://0057-soc-qcom-Introduce-WCNSS_CTRL-SMD-client.patch"
SRC_URI += "file://0058-reset-hi6220-fix-modular-build.patch"
SRC_URI += "file://0059-memory-tegra-Add-number-of-TLB-lines-for-Tegra124.patch"
SRC_URI += "file://0060-soc-qcom-smd-rpm-Add-existing-platform-support.patch"
SRC_URI += "file://0061-MAINTAINERS-Change-QCOM-entries.patch"
SRC_URI += "file://0062-soc-mediatek-SCPSYS-Add-regulator-support.patch"
SRC_URI += "file://0063-drivers-soc-make-mediatek-mtk-scpsys.c-explicitly-no.patch"
SRC_URI += "file://0064-ARM-bcm2835-Define-two-new-packets-from-the-latest-f.patch"
SRC_URI += "file://0065-dt-bindings-add-rpi-power-domain-driver-bindings.patch"
SRC_URI += "file://0066-bus-uniphier-system-bus-add-UniPhier-System-Bus-driv.patch"
SRC_URI += "file://0067-MAINTAINERS-Drop-Kumar-Gala-from-QCOM.patch"
SRC_URI += "file://0068-ARM-bcm2835-clarify-RASPBERRYPI_FIRMWARE-dependency.patch"
SRC_URI += "file://0069-bus-uniphier-allow-only-built-in-driver.patch"
SRC_URI += "file://0070-minor-re-adjustment-of-drivers-soc-Kconfig-and-Makef.patch"
SRC_URI += "file://0071-soc-fsl-Introduce-the-Freescale-DPAA-resource-manage.patch"
SRC_URI += "file://0072-soc-fsl-Introduce-DPAA-BMan-device-management-driver.patch"
SRC_URI += "file://0073-soc-fsl-Introduce-the-DPAA-BMan-portal-driver.patch"
SRC_URI += "file://0074-soc-bman-Add-debugfs-support-for-the-BMan-driver.patch"
SRC_URI += "file://0075-soc-bman-Add-self-tester-for-BMan-driver.patch"
SRC_URI += "file://0076-soc-fsl-Introduce-drivers-for-the-DPAA-QMan.patch"
SRC_URI += "file://0077-soc-qman-Add-self-tester-for-QMan-driver.patch"
SRC_URI += "file://0078-soc-qman-Add-debugfs-support-for-the-QMan-driver.patch"
SRC_URI += "file://0079-soc-qman-Add-HOTPLUG_CPU-support-to-the-QMan-driver.patch"
SRC_URI += "file://0080-devres-add-devm_alloc_percpu.patch"
SRC_URI += "file://0081-dpaa_eth-add-support-for-DPAA-Ethernet.patch"
SRC_URI += "file://0082-dpaa_eth-add-support-for-S-G-frames.patch"
SRC_URI += "file://0083-dpaa_eth-add-driver-s-Tx-queue-selection-mechanism.patch"
SRC_URI += "file://0084-dpaa_eth-add-ethtool-functionality.patch"
SRC_URI += "file://0085-dpaa_eth-add-ethtool-statistics.patch"
SRC_URI += "file://0086-dpaa_eth-add-sysfs-exports.patch"
SRC_URI += "file://0087-dpaa_eth-add-trace-points.patch"
SRC_URI += "file://0088-incompletely-ported-powerpc-86xx-Switch-to-kconfig-f.patch"
SRC_URI += "file://0089-powerpc-corenet-Enable-FSL_PAMU.patch"
SRC_URI += "file://0090-powerpc-corenet-Enable-FSL_-BQ-MAN.patch"
SRC_URI += "file://0091-powerpc-corenet-Enable-FSL_FMAN.patch"
SRC_URI += "file://0092-powerpc-corenet-Enable-FSL_DPAA_ETH.patch"
SRC_URI += "file://0093-fsl-fman-Workaround-for-Errata-A-007273.patch"
SRC_URI += "file://0094-fsl-fman-fix-loadable-module-compilation.patch"
SRC_URI += "file://0095-fsl-fman-address-sparse-warnings.patch"
SRC_URI += "file://0096-powerpc-fsl-Update-fman-dt-binding-with-pcs-phy-and-.patch"
SRC_URI += "file://0097-powerpc-mpc85xx-Add-pcsphy-nodes-to-FManV3-device-tr.patch"
SRC_URI += "file://0098-move-tqmt1042-device-tree-file-to-fsl-folder-for-con.patch"
SRC_URI += "file://0099-Documentation-dt-binding-fsl-add-devicetree-binding-.patch"
SRC_URI += "file://0100-powerpc-mm-any-thread-in-one-core-can-be-the-first-t.patch"
SRC_URI += "file://0101-powerpc-cache-add-cache-flush-operation-for-various-.patch"
SRC_URI += "file://0102-ported-QE-Move-QE-from-arch-powerpc-to-drivers-soc-c.patch"
SRC_URI += "file://0103-powerpc-rcpm-add-RCPM-driver.patch"
SRC_URI += "file://0104-powerpc-mpc85xx-refactor-the-PM-operations.patch"
SRC_URI += "file://0105-powerpc-mpc85xx-Add-hotplug-support-on-E5500-and-E50.patch"
SRC_URI += "file://0106-applied-1b04a533afe20f851d372d58c907aafbb7d6589b-fro.patch"
SRC_URI += "file://0107-update-TQMT1042-defconfig-to-current-state-of-develo.patch"
SRC_URI += "file://0108-TQMT1042-defconfig-Add-defconfig-for-64-bit-mode.patch"
SRC_URI += "file://0109-phy-dp83867-Make-rgmii-parameters-optional.patch"
SRC_URI += "file://0110-phy-dp83867-Fix-compilation-with-CONFIG_OF_MDIO-m.patch"
SRC_URI += "file://0111-TQMT1042-add-proper-NOR-Flash-partition-table.patch"
SRC_URI += "file://0112-TQMT1042-cleanup-and-correct-ethernet-device-tree-no.patch"
SRC_URI += "file://0113-TQMT1042-remove-unneccessary-ethernet-phy-drivers-fr.patch"
SRC_URI += "file://0114-update-TQMT1042_64-defconfig-to-current-state-of-dev.patch"
SRC_URI += "file://0115-Revert-phy-dp83867-Fix-compilation-with-CONFIG_OF_MD.patch"
SRC_URI += "file://0116-Revert-phy-dp83867-Make-rgmii-parameters-optional.patch"
SRC_URI += "file://0117-update-TQMT1042_64-defconfig-to-current-state-of-dev.patch"
SRC_URI += "file://0118-update-TQMT1042-defconfig-to-current-state-of-develo.patch"
SRC_URI += "file://0119-add-developer-copyright-in-device-tree-file.patch"
SRC_URI += "file://0120-Workaround-to-enable-rgmii-id-phy-connection-type-wi.patch"
SRC_URI += "file://0121-T104xRDB-dts-Add-layer-2-switch.patch"
SRC_URI += "file://0122-cosmetic-TQMT1042RDB-dts-Fix-whitespaces.patch"
SRC_URI += "file://0123-cosmetic-85xx-tqmt1042_defconfig-Reduced-by-make-sav.patch"
SRC_URI += "file://0124-cosmetic-85xx-tqmt1042_64_defconfig-Reduced-by-make-.patch"
SRC_URI += "file://0125-85xx-tqmt1042_defconfig-Change-all-modules-to-be-bui.patch"
SRC_URI += "file://0126-85xx-tqmt1042_64_defconfig-Change-all-modules-to-be-.patch"
SRC_URI += "file://0127-phy-Add-phydev_err-and-phydev_dbg-macros.patch"
SRC_URI += "file://0128-Revert-Revert-phy-dp83867-Make-rgmii-parameters-opti.patch"
SRC_URI += "file://0129-tqmt1042.dts-Fix-NOR-partition-table.patch"
SRC_URI += "file://0130-tqmt1042.dts-add-descirptive-commentary-and-minor-co.patch"
SRC_URI += "file://0131-TQMT1042-add-new-device-tree-variant-tqmt1042_serdes.patch"
SRC_URI += "file://0132-tqmt1040.dts-add-initial-TQMT1040-device-tree.patch"
SRC_URI += "file://0133-TQMT1040-add-TQMT1040-machine-definition-in-corenet_.patch"
SRC_URI += "file://0134-tqmt1040.dts-edit-device-tree-to-feature-T1040-l2swi.patch"
SRC_URI += "file://0135-TQMT104x-add-DIU-support-for-TQMT1040-and-TQMT1042.patch"
SRC_URI += "file://0136-TQMT104x-add-bootup-logo-in-64bit-defconfig.patch"
SRC_URI += "file://0137-DP83867-ethernet-phy-driver-add-clock-output-select.patch"
SRC_URI += "file://0138-TQMT104x-device-trees-set-EC2-RGMII-phy-to-output-12.patch"
SRC_URI += "file://0139-TQMT104x-add-full-DP83867-properties-in-device-tree.patch"
SRC_URI += "file://0140-TQMT1042_SERDES8E-dts-set-EC2-RGMII-phy-to-output-12.patch"
SRC_URI += "file://0141-TQMT1042_SERDES8E-add-full-DP83867-properties-in-dev.patch"
SRC_URI += "file://0142-cosmetic-TQMT104x-correct-comments-in-dts-headers.patch"
SRC_URI += "file://0143-gitignore-add-defconfig-file.patch"
SRC_URI += "file://0144-TQMT2081-add-32bit-defconfig.patch"
SRC_URI += "file://0145-TQMT2081-add-64bit-defconfig.patch"
SRC_URI += "file://0146-TQMT2081-64bit-defconfig-add-remove-TQMT2081-relevan.patch"
SRC_URI += "file://0147-TQMT2081-add-initial-device-tree-and-SOM-definition-.patch"
SRC_URI += "file://0148-TQMT2081-dts-remove-accidental-include-of-t2080rdb-d.patch"
SRC_URI += "file://0149-TQMT2081-dts-add-xfi_tx_dis-gpio-hog-extend-i2c-comm.patch"
SRC_URI += "file://0150-TQMT2081-dts-add-MAC9-XFI-device-tree-node.patch"
SRC_URI += "file://0151-cosmetic-TQMT1040-dts-remove-unneccary-whitespaces.patch"
SRC_URI += "file://0152-cosmetic-TQMT1042-dts-remove-unneccary-whitespaces.patch"
